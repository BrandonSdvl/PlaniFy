package com.mew.planify

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.mew.planify.data.local.AppDatabase
import com.mew.planify.ui.screens.AppNavigation
import com.mew.planify.ui.theme.PlaniFyTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        applicationContext.deleteDatabase("planifydb")
        val db = AppDatabase.getDatabase(applicationContext)

        setContent {
            PlaniFyTheme {
                AppNavigation(db)
            }
        }
    }
}
