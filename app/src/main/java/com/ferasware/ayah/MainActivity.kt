package com.ferasware.ayah

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferasware.ayah.ui.screens.AyahViewModel
import com.ferasware.ayah.ui.theme.AyahTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AyahTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val viewModel: AyahViewModel = viewModel(factory = AyahViewModel.Factory)
                    AyahApp(viewModel = viewModel)
                }
            }
        }
    }
}