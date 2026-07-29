package com.echojournal.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.echojournal.app.repository.SortOption
import com.echojournal.app.sync.DriveSyncManager
import com.echojournal.app.ui.components.EntryCard
import com.echojournal.app.ui.components.NewEntrySheet
import com.echojournal.app.viewmodel.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalApp(viewModel: JournalViewModel) {
    val context = LocalContext.current
    val entries by viewModel.entries.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val isSignedIn by viewModel.isDriveSignedIn.collectAsState()

    var showNewEntrySheet by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    val signInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.refreshDriveSignInState()
        viewModel.triggerSync()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Echo Journal") },
                actions = {
                    IconButton(onClick = {
                        if (isSignedIn) {
                            viewModel.triggerSync()
                        } else {
                            signInLauncher.launch(DriveSyncManager.buildSignInClient(context).signInIntent)
                        }
                    }) {
                        Icon(
                            if (isSignedIn) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = if (isSignedIn) "Synced with Google Drive" else "Sign in to sync with Google Drive"
                        )
                    }
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort entries")
                        }
                        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                            DropdownMenuItem(text = { Text("Newest first") }, onClick = {
                                viewModel.setSort(SortOption.NEWEST); showSortMenu = false
                            })
                            DropdownMenuItem(text = { Text("Oldest first") }, onClick = {
                                viewModel.setSort(SortOption.OLDEST); showSortMenu = false
                            })
                            DropdownMenuItem(text = { Text("By type") }, onClick = {
                                viewModel.setSort(SortOption.TYPE); showSortMenu = false
                            })
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewEntrySheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "New entry")
            }
        }
    ) { padding ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No entries yet.\nTap + to write, record, or add a photo/video.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    EntryCard(entry = entry, onDelete = { viewModel.deleteEntry(entry) })
                }
            }
        }
    }

    if (showNewEntrySheet) {
        NewEntrySheet(
            onDismiss = { showNewEntrySheet = false },
            onAddText = { viewModel.addTextEntry(it) },
            onAddVoice = { path, duration -> viewModel.addVoiceEntry(path, duration) },
            onAddImage = { path -> viewModel.addImageEntry(path) },
            onAddVideo = { path -> viewModel.addVideoEntry(path) }
        )
    }
}
