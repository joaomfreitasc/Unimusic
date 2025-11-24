package com.example.unimusicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = PrimaryGreen,
                    background = DeepBlack,
                    surface = SurfaceGray,
                    onPrimary = Color.Black,
                    onSurface = Color.White,
                    secondary = SecondaryPurple
                ),
                typography = Typography(
                    headlineMedium = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 0.5.sp
                    ),
                    titleMedium = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.SemiBold, fontSize = 18.sp
                    ),
                    bodyMedium = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Normal, fontSize = 16.sp
                    )
                )
            ) {
                UnimusicApp()
            }
        }
    }
}

