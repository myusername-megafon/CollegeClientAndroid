package com.example.collegeclientandroid.viewmodel

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@kotlinx.serialization.Serializable
data class BypassTask(
    val id: String,
    val title: String,
    val done: Boolean = false,
    val note: String = ""
)

@HiltViewModel
class BypassSheetViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val prefs = context.getSharedPreferences("bypass_sheet_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    private val key = "custom_bypass_tasks"

    private val _tasks = MutableStateFlow(loadTasks())
    val tasks: StateFlow<List<BypassTask>> = _tasks.asStateFlow()

    fun toggleTask(taskId: String, checked: Boolean) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) task.copy(done = checked) else task
        }
        saveTasks()
    }

    fun updateNote(taskId: String, note: String) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) task.copy(note = note) else task
        }
        saveTasks()
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        val task = BypassTask(
            id = System.currentTimeMillis().toString(),
            title = title.trim()
        )
        _tasks.value = listOf(task) + _tasks.value
        saveTasks()
    }

    fun removeTask(taskId: String) {
        _tasks.value = _tasks.value.filterNot { it.id == taskId }
        saveTasks()
    }

    private fun loadTasks(): List<BypassTask> {
        val raw = prefs.getString(key, null)
        if (raw.isNullOrBlank()) {
            return listOf(
                BypassTask("library", "Сдать книги в библиотеку"),
                BypassTask("accounting", "Закрыть вопросы с бухгалтерией")
            )
        }
        return runCatching { json.decodeFromString<List<BypassTask>>(raw) }.getOrDefault(emptyList())
    }

    private fun saveTasks() {
        prefs.edit {
            putString(key, json.encodeToString(_tasks.value))
        }
    }
}
