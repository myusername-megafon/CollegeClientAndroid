package com.example.collegeclientandroid

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.collegeclientandroid.ui.theme.CollegeClientAndroidTheme
import com.example.collegeclientandroid.view.LoginScreen
import com.example.collegeclientandroid.view.RegistrationScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.collegeclientandroid.view.HomeScreen
import com.example.collegeclientandroid.view.ProfileScreen
import com.example.collegeclientandroid.viewmodel.MainViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CollegeClientAndroidTheme {
                SetSystemBarsColor()
                Scaffold(modifier = Modifier.statusBarsPadding().fillMaxSize()) {
                    it
                    val navController = rememberNavController()
                    val mainViewModel: MainViewModel = hiltViewModel()
                    var startDestination by remember { mutableStateOf("login") }
                    
                    LaunchedEffect(Unit) {
                        startDestination = if (mainViewModel.isUserLoggedIn()) "home" else "login"
                    }
                    
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("login") {
                            LoginScreen(
                                onRegisterClick = { navController.navigate("registration") },
                                onLoginSuccess = { navController.navigate("home") }
                            )
                        }
                        composable("registration") {
                            RegistrationScreen(
                                onLoginClick = { navController.popBackStack(); navController.navigate("login") },
                                onRegistrationSuccess = { navController.navigate("home") }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                onProfileClick = {navController.navigate("profile")}
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                onBackClick = { navController.navigate("home") },
                                onLogoutClick = { navController.navigate("login") }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SetSystemBarsColor() {
        val view = LocalView.current
        val useDarkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5f

        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = Color.Transparent.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkIcons
        }
    }
}
