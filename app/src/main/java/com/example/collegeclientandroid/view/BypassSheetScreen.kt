package com.example.collegeclientandroid.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import com.example.collegeclientandroid.viewmodel.BypassSheetViewModel

@Composable
fun BypassSheetScreen(
    onBackClick: () -> Unit,
    viewModel: BypassSheetViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    var newTask by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Обходной лист",
                style = MaterialTheme.typography.titleLarge
            )
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Назад")
            }
        }

        Spacer(modifier = Modifier.size(12.dp))
        Text("Данные сохраняются только на вашем устройстве.")
        Spacer(modifier = Modifier.size(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTask,
                onValueChange = { newTask = it },
                modifier = Modifier.weight(1f),
                label = { Text("Новое задание") }
            )
            Spacer(modifier = Modifier.size(8.dp))
            Button(onClick = {
                viewModel.addTask(newTask)
                newTask = ""
            }) {
                Text("Добавить")
            }
        }
        Spacer(modifier = Modifier.size(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tasks) { task ->
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.done,
                            onCheckedChange = { viewModel.toggleTask(task.id, it) }
                        )
                        Text(task.title, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.removeTask(task.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                    OutlinedTextField(
                        value = task.note,
                        onValueChange = { viewModel.updateNote(task.id, it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Комментарий") }
                    )
                }
            }
        }
    }
}
