package com.example.luontopeli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.luontopeli.ui.navigation.LuontopeliNavigation
import com.example.luontopeli.ui.theme.LuontopeliTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LuontopeliTheme {
                LuontopeliNavigation()
            }
        }
    }
}