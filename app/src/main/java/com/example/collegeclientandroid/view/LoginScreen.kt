package com.example.collegeclientandroid.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegeclientandroid.ui.theme.CollegeClientAndroidTheme
import com.example.collegeclientandroid.viewmodel.LoginScreenViewModel

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.screenState.collectAsState()
    CollegeClientAndroidTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(72.dp))

            Text(text = "Вход", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.size(48.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.9f)
                    .fillMaxHeight(fraction = 0.6f)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
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
                    Button(
                        modifier = Modifier
                            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                            .fillMaxWidth(),
                        onClick = { onLoginClick()/*viewModel.login()*/ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.White),
                        enabled = !uiState.isLoading
                    )
                    {
                        Text(text = "Войти", color = Color.White)
                    }

                    Text(
                        text = "Нет аккаунта? Зарегистрируйтесь",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.clickable { onRegisterClick() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Превью без DI
}


