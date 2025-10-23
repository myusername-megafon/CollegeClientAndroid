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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegeclientandroid.ui.theme.CollegeClientAndroidTheme
import com.example.collegeclientandroid.viewmodel.RegistrationScreenViewModel

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

                    Button(
                        modifier = Modifier
                            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                            .fillMaxWidth(),
                        onClick = { viewModel.register() },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.White),
                        enabled = !uiState.isLoading
                    )
                    {
                        Text(text = if (uiState.isLoading) "Регистрация..." else "Регистрация", color = Color.White)
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
}
