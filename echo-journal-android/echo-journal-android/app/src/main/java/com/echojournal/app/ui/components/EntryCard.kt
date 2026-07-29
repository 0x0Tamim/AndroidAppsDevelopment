package com.echojournal.app.ui.components

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.echojournal.app.data.Entry
import com.echojournal.app.data.EntryType
import com.echojournal.app.ui.theme.ImageAccent
import com.echojournal.app.ui.theme.TextAccent
import com.echojournal.app.ui.theme.VideoAccent
import com.echojournal.app.ui.theme.VoiceAccent
import com.echojournal.app.util.DateUtils
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryCard(entry: Entry, onDelete: () -> Unit) {
    val accentColor = when (entry.type) {
        EntryType.TEXT -> TextAccent
        EntryType.VOICE -> VoiceAccent
        EntryType.IMAGE -> ImageAccent
        EntryType.VIDEO -> VideoAccent
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${DateUtils.formatDate(entry.createdAt)} · ${DateUtils.formatTime(entry.createdAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete entry")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (entry.type) {
                EntryType.TEXT -> {
                    Text(text = entry.text ?: "", style = MaterialTheme.typography.bodyLarge)
                }
                EntryType.VOICE -> {
                    VoicePlayerRow(entry)
                    if (!entry.text.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = entry.text, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                EntryType.IMAGE -> {
                    AsyncImage(
                        model = entry.mediaPath?.let { Uri.fromFile(java.io.File(it)) },
                        contentDescription = "Image entry",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 260.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    if (!entry.text.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = entry.text, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                EntryType.VIDEO -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 260.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                .data(entry.mediaPath?.let { Uri.fromFile(java.io.File(it)) })
                                .videoFrameMillis(1000)
                                .build(),
                            contentDescription = "Video entry",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Icon(
                            Icons.Default.Movie,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(48.dp)
                        )
                    }
                    if (!entry.text.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = entry.text, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun VoicePlayerRow(entry: Entry) {
    var isPlaying by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(entry.id) {
        onDispose {
            player?.release()
            player = null
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                if (isPlaying) {
                    player?.pause()
                    isPlaying = false
                } else {
                    if (player == null) {
                        player = MediaPlayer().apply {
                            setDataSource(entry.mediaPath)
                            prepare()
                            setOnCompletionListener { isPlaying = false }
                        }
                    }
                    player?.start()
                    isPlaying = true
                }
            },
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(VoiceAccent.copy(alpha = 0.15f))
        ) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = VoiceAccent
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Voice note · ${DateUtils.formatDuration(entry.durationMs)}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
