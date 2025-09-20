package com.tuapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tuapp.ui.NavRoot   // 👈 aquí conectamos NavRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavRoot()   // 👈 aquí usamos NavRoot
        }
    }
}
