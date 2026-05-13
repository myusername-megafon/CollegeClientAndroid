package com.example.collegeclientandroid.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Полноэкранный блок при [maintenanceEnabled]: без кнопок выхода, системная «назад» игнорируется.
 */
@Composable
fun MaintenanceBlocker(
    maintenanceEnabled: Boolean,
    message: String,
    modifier: Modifier = Modifier
) {
    if (!maintenanceEnabled) return

    BackHandler { /* блокируем выход */ }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0f172a)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Технические работы",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = message.ifBlank { "Сервис временно недоступен. Попробуйте позже." },
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF94a3b8),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
