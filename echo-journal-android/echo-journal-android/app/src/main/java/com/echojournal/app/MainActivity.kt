package com.echojournal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.echojournal.app.ui.JournalApp
import com.echojournal.app.ui.theme.EchoJournalTheme
import com.echojournal.app.viewmodel.JournalViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: JournalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EchoJournalTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    JournalApp(viewModel = viewModel)
                }
            }
        }
    }
}
