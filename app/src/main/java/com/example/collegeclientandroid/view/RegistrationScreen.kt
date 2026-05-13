package com.example.collegeclientandroid.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegeclientandroid.ui.theme.CollegeClientAndroidTheme
import com.example.collegeclientandroid.viewmodel.RegistrationScreenViewModel
import com.example.collegeclientandroid.help.InfoSystemHelpModule
import com.example.collegeclientandroid.help.ScreenHelpAction

@Composable
fun RegistrationScreen(
    onLoginClick: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    viewModel: RegistrationScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.screenState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegistrationSuccess()
        }
    }
    
    CollegeClientAndroidTheme {
        if (!uiState.isCodeSent) {
            Box(modifier = Modifier.fillMaxSize()) {
                ScreenHelpAction(
                    module = InfoSystemHelpModule.REGISTRATION,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 12.dp, end = 12.dp)
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Spacer(modifier = Modifier.size(72.dp))

                Text(text = "Регистрация", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.size(48.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.9f)
                        .fillMaxHeight(fraction = 0.95f)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                Text(
                    text = "ФИО",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 24.dp, top = 32.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 24.dp, top = 4.dp, end = 24.dp)
                        .fillMaxWidth(),
                    value = uiState.fullName,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = { viewModel.onFullNameChange(it) })

                Text(
                    text = "Группа",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 24.dp, top = 32.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 24.dp, top = 4.dp, end = 24.dp)
                        .fillMaxWidth(),
                    value = uiState.group,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = { viewModel.onGroupChange(it) })

                Text(
                    text = "Электронная почта",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 24.dp, top = 32.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 24.dp, top = 4.dp, end = 24.dp)
                        .fillMaxWidth(),
                    value = uiState.email,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = { viewModel.onEmailChange(it) })

                Text(
                    text = "Пароль",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 24.dp, top = 32.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 24.dp, top = 4.dp, end = 24.dp)
                        .fillMaxWidth(),
                    value = uiState.password,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = { viewModel.onPasswordChange(it) })
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    uiState.errorMessage?.let { errorMessage ->
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                    uiState.infoMessage?.let { info ->
                        Text(
                            text = info,
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    Button(
                        modifier = Modifier
                            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                            .fillMaxWidth(),
                        onClick = {
                            if (uiState.isCodeSent) viewModel.verifyCode() else viewModel.register()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.White),
                        enabled = !uiState.isLoading
                    )
                    {
                        val buttonText = when {
                            uiState.isLoading -> "Подождите..."
                            else -> "Отправить код"
                        }
                        Text(text = buttonText, color = Color.White)
                    }

                    Text(
                        text = "Уже есть аккаунт?",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
            }
            }
        } else {
            val cooldownProgress by animateFloatAsState(
                targetValue = (uiState.cooldownSeconds / 30f).coerceIn(0f, 1f),
                label = "cooldownProgress"
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 12.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.closeVerification() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    ScreenHelpAction(module = InfoSystemHelpModule.EMAIL_VERIFICATION)
                }
                Spacer(modifier = Modifier.size(72.dp))
                Text(text = "Подтверждение email", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.size(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.9f)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Введите код из почты, отправленный на адрес:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = uiState.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(16.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.verificationCode,
                        label = { Text("Код подтверждения") },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        onValueChange = { viewModel.onVerificationCodeChange(it) }
                    )
                    Spacer(modifier = Modifier.size(10.dp))

                    LinearProgressIndicator(
                        progress = { cooldownProgress },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = if (uiState.cooldownSeconds > 0)
                            "Повторная отправка через ${uiState.cooldownSeconds} сек"
                        else
                            "Можно отправить код повторно",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.size(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.resendCode() },
                            enabled = !uiState.isLoading && uiState.cooldownSeconds == 0,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text("Отправить код повторно")
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Button(
                        onClick = { viewModel.verifyCode() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(if (uiState.isLoading) "Проверка..." else "Подтвердить email", color = Color.White)
                    }

                    uiState.errorMessage?.let { errorMessage ->
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = errorMessage, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
                    }
                    uiState.infoMessage?.let { info ->
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = info, color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
