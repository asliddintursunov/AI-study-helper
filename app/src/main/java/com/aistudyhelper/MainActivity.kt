package com.aistudyhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aistudyhelper.navigation.AIStudyHelperApp
import com.aistudyhelper.ui.theme.AIStudyHelperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIStudyHelperTheme {
                AIStudyHelperApp()
            }
        }
    }
}
