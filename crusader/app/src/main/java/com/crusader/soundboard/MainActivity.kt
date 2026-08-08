package com.crusader.soundboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crusader.soundboard.ui.SoundboardApp
import com.crusader.soundboard.ui.SoundboardTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundboardTheme {
                val viewModel: MainViewModel = viewModel()
                SoundboardApp(viewModel)
            }
        }
    }
}
