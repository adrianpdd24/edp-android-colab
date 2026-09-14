package com.liceo.liceochat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.liceo.liceochat.ui.ChatScreen
import com.liceo.liceochat.ui.theme.LiceoChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiceoChatTheme {
                ChatScreen()
            }
        }
    }
}
