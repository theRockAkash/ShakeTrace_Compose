package com.therockakash.shaketrace.compose.shake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.therockakash.shaketrace.compose.ShakeTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShakeComposeActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                var refreshLogs by remember { mutableIntStateOf(1) }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                }
                            },
                            title = { Text("Logs") },
                            actions = {
                                IconButton(onClick = {
                                    ShakeTrace.clearLogs()
                                    refreshLogs++
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Clear log")
                                }
                            })
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val scrollState = rememberScrollState()
                    val scope = rememberCoroutineScope()
                    var logs by remember { mutableStateOf("") }

                    LaunchedEffect(refreshLogs) {
                        scope.launch(Dispatchers.IO) {
                            logs = ShakeTrace.getLogs()
                        }.invokeOnCompletion {
                            scope.launch {
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                        }
                    }
                    Text(
                        text = logs,
                        modifier = Modifier
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                    )
                }
            }
        }
    }
}

