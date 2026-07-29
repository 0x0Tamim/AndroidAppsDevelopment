package com.echojournal.app.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.echojournal.app.util.AudioRecorderManager
import com.echojournal.app.util.DateUtils
import com.echojournal.app.util.FileUtils
import kotlinx.coroutines.delay

enum class NewEntryMode { PICK_TYPE, TEXT, RECORDING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntrySheet(
    onDismiss: () -> Unit,
    onAddText: (String) -> Unit,
    onAddVoice: (path: String, durationMs: Long) -> Unit,
    onAddImage: (path: String) -> Unit,
    onAddVideo: (path: String) -> Unit
) {
    val context = LocalContext.current
    var mode by remember { mutableStateOf(NewEntryMode.PICK_TYPE) }
    var textValue by remember { mutableStateOf("") }
    val recorder = remember { AudioRecorderManager(context) }
    var recordingMs by remember { mutableStateOf(0L) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val path = FileUtils.copyUriToAppStorage(context, it, "jpg")
            if (path != null) onAddImage(path)
        }
        onDismiss()
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val path = FileUtils.copyUriToAppStorage(context, it, "mp4")
            if (path != null) onAddVideo(path)
        }
        onDismiss()
    }
    val micPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            recorder.start()
            mode = NewEntryMode.RECORDING
        } else {
            onDismiss()
        }
    }

    LaunchedEffect(mode) {
        if (mode == NewEntryMode.RECORDING) {
            recordingMs = 0L
            while (mode == NewEntryMode.RECORDING) {
                delay(1000)
                recordingMs += 1000
            }
        }
    }

    ModalBottomSheet(onDismissRequest = {
        if (recorder.isRecording()) recorder.cancel()
        onDismiss()
    }) {
        Box(modifier = Modifier.padding(24.dp).defaultMinSize(minHeight = 160.dp)) {
            when (mode) {
                NewEntryMode.PICK_TYPE -> {
                    Column {
                        Text("New entry", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            EntryTypeButton(Icons.Default.Mic, "Voice") {
                                micPermission.launch(android.Manifest.permission.RECORD_AUDIO)
                            }
                            EntryTypeButton(Icons.Default.Image, "Photo") {
                                imagePicker.launch("image/*")
                            }
                            EntryTypeButton(Icons.Default.Videocam, "Video") {
                                videoPicker.launch("video/*")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = textValue,
                            onValueChange = { textValue = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Write what's on your mind...") },
                            minLines = 3
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (textValue.isNotBlank()) {
                                    onAddText(textValue.trim())
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = textValue.isNotBlank()
                        ) {
                            Text("Save entry")
                        }
                    }
                }
                NewEntryMode.RECORDING -> {
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Recording...", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(DateUtils.formatDuration(recordingMs), style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(24.dp))
                        FilledIconButton(
                            onClick = {
                                val result = recorder.stop()
                                if (result != null) {
                                    onAddVoice(result.first.absolutePath, result.second)
                                }
                                onDismiss()
                            },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop recording")
                        }
                    }
                }
                NewEntryMode.TEXT -> {}
            }
        }
    }
}

@Composable
private fun EntryTypeButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        FilledTonalIconButton(onClick = onClick, modifier = Modifier.size(56.dp)) {
            Icon(icon, contentDescription = label)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
