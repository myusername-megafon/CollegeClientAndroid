package com.example.collegeclientandroid

import android.app.Activity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
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
import androidx.core.content.ContextCompat
import android.os.Build
import android.net.Uri
import com.example.collegeclientandroid.view.BypassSheetScreen
import com.example.collegeclientandroid.view.MaintenanceBlocker
import com.example.collegeclientandroid.view.StudyPlanScreen
import com.example.collegeclientandroid.push.PushTokenManager
import com.example.collegeclientandroid.viewmodel.ClientAppUiState
import com.example.collegeclientandroid.viewmodel.ClientConfigViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var pushTokenManager: PushTokenManager

    private val clientConfigViewModel: ClientConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching { pushTokenManager.registerCurrentDeviceToken() }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CollegeClientAndroidTheme {
                SetSystemBarsColor()
                RequestNotificationPermission()

                var clientUi by remember { mutableStateOf(ClientAppUiState()) }
                LaunchedEffect(clientConfigViewModel) {
                    clientConfigViewModel.uiState.collect { clientUi = it }
                }

                Box(Modifier.fillMaxSize()) {
                    Scaffold(modifier = Modifier.statusBarsPadding().fillMaxSize()) {
                        val navController = rememberNavController()
                        val mainViewModel: MainViewModel = hiltViewModel()
                        // Стартовый маршрут задаём синхронно: смена startDestination после первого кадра
                        // ломает граф NavHost и часто приводит к падению при запуске.
                        val startDestination = remember(mainViewModel) {
                            if (mainViewModel.isUserLoggedIn()) "home" else "login"
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
                                    onLoginClick = {
                                        navController.popBackStack()
                                        navController.navigate("login")
                                    },
                                    onRegistrationSuccess = { navController.navigate("home") }
                                )
                            }
                            composable("home") {
                                HomeScreen(
                                    onProfileClick = { navController.navigate("profile") },
                                    onBypassSheetClick = { navController.navigate("bypass") },
                                    onStudyPlanClick = { group ->
                                        navController.navigate("studyPlan/${Uri.encode(group)}")
                                    }
                                )
                            }
                            composable("profile") {
                                ProfileScreen(
                                    onBackClick = { navController.navigate("home") },
                                    onLogoutClick = { navController.navigate("login") }
                                )
                            }
                            composable("bypass") {
                                BypassSheetScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable("studyPlan/{group}") { backStackEntry ->
                                val group = Uri.decode(backStackEntry.arguments?.getString("group").orEmpty())
                                StudyPlanScreen(
                                    group = group,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }

                    MaintenanceBlocker(
                        maintenanceEnabled = clientUi.maintenanceEnabled,
                        message = clientUi.maintenanceMessage
                    )
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

    @Composable
    private fun RequestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {}
        )

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
