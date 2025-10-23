package com.example.collegeclientandroid.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegeclientandroid.ui.theme.CollegeClientAndroidTheme
import com.example.collegeclientandroid.viewmodel.HomeScreenViewModel
import com.example.collegeclientandroid.viewmodel.LoginScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onProfileClick: () -> Unit
) {

    val uiState by viewModel.screenState.collectAsState()
    val datePickerState = rememberDatePickerState()

    CollegeClientAndroidTheme {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Расписание",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp)
                )
                Button(
                    onClick = { onProfileClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = Color.White
                    )
                )
                {
                    Icon(Icons.Default.AccountCircle, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Профиль", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = "Группа",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.groupName,
                onValueChange = { viewModel.onGroupNameChanged(it) },
                label = { Text("Введите номер группы") },
                isError = uiState.errorMessage != null
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = "Дата",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onDatePickerClicked() },
                value = uiState.selectedDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Выберите дату") },
                trailingIcon = {
                    IconButton(onClick = { viewModel.onDatePickerClicked() }) {
                        Icon(Icons.Default.Edit, contentDescription = "Выбрать дату")
                    }
                },
                isError = uiState.errorMessage != null
            )

            Spacer(modifier = Modifier.size(16.dp))

            Button(
                onClick = {
                    if (uiState.groupName.isNotBlank() && uiState.selectedDateMillis != null) {
                        viewModel.loadSchedule(uiState.groupName, uiState.selectedDateMillis!!)
                    } else {
                        viewModel.setError("Заполните все поля")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContentColor = Color.Black,
                    disabledContainerColor = Color.DarkGray
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                Text("Найти расписание", color = Color.White)
            }

            Spacer(modifier = Modifier.size(16.dp))

            if (uiState.schedule.isNotEmpty()) {
                Text(
                    text = "Расписание на ${uiState.selectedDate}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.schedule) { lesson ->
                        LessonCard(lesson = lesson)
                    }
                }
            }
        }

        if (uiState.showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { viewModel.onDatePickerDismiss() },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { dateMillis ->
                                viewModel.onDateSelected(dateMillis)
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onDatePickerDismiss() }
                    ) {
                        Text("Отмена")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun LessonCard(lesson: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = lesson,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


