package com.emi.wac

import com.emi.wac.ui.screens.MainScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.emi.wac.ui.theme.WACTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WACTheme {
                MainScreen()
            }
        }
    }
}
