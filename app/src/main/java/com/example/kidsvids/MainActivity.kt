package com.example.kidsvids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider // Import this
import androidx.navigation.compose.rememberNavController
import com.example.kidsvids.data.DatabaseProvider
import com.example.kidsvids.ui.theme.KidsVidsTheme
import com.example.kidsvids.uiscreens.AppNavGraph
import com.example.kidsvids.viewmodels.AppViewModelFactory
import com.example.kidsvids.viewmodels.SessionViewModel

class MainActivity : ComponentActivity() {

    // Create the SessionViewModel using the AndroidViewModelFactory
    private val sessionViewModel: SessionViewModel by lazy {
        ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[SessionViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KidsVidsTheme {
                val database = DatabaseProvider.getDatabase(applicationContext)

                // Create our factory, giving it the database and the sessionViewModel
                val factory = AppViewModelFactory(database, sessionViewModel)

                val navController = rememberNavController()

                // Pass the factory AND the sessionViewModel to the NavGraph
                AppNavGraph(
                    navController = navController,
                    factory = factory,
                    sessionViewModel = sessionViewModel
                )
            }
        }
    }
}