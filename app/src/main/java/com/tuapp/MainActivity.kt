package com.tuapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tuapp.ui.NavRoot   // 👈 Importamos NavRoot desde la carpeta ui

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavRoot()   // 👈 Aquí cargamos la pantalla de navegación
        }
    }
}
