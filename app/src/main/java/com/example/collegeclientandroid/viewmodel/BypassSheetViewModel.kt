package com.example.collegeclientandroid.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject

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
    companion object {
        private const val TAG = "BypassSheetVM"
    }
    private val prefs = context.getSharedPreferences("bypass_sheet_prefs", Context.MODE_PRIVATE)
    private val key = "custom_bypass_tasks"

    private val _tasks = MutableStateFlow(loadTasks())
    val tasks: StateFlow<List<BypassTask>> = _tasks.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun toggleTask(taskId: String, checked: Boolean) {
        runCatching {
            _tasks.value = _tasks.value.map { task ->
                if (task.id == taskId) task.copy(done = checked) else task
            }
            saveTasks()
        }.onFailure {
            Log.e(TAG, "toggleTask failed", it)
            _error.value = "Не удалось обновить задачу"
        }
    }

    fun updateNote(taskId: String, note: String) {
        runCatching {
            _tasks.value = _tasks.value.map { task ->
                if (task.id == taskId) task.copy(note = note) else task
            }
            saveTasks()
        }.onFailure {
            Log.e(TAG, "updateNote failed", it)
            _error.value = "Не удалось сохранить комментарий"
        }
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        runCatching {
            val task = BypassTask(
                id = UUID.randomUUID().toString(),
                title = title.trim()
            )
            _tasks.value = listOf(task) + _tasks.value
            saveTasks()
        }.onFailure {
            Log.e(TAG, "addTask failed", it)
            _error.value = "Не удалось добавить задачу"
        }
    }

    fun removeTask(taskId: String) {
        runCatching {
            _tasks.value = _tasks.value.filterNot { it.id == taskId }
            saveTasks()
        }.onFailure {
            Log.e(TAG, "removeTask failed", it)
            _error.value = "Не удалось удалить задачу"
        }
    }

    private fun loadTasks(): List<BypassTask> {
        val raw = prefs.getString(key, null)
        if (raw.isNullOrBlank()) {
            return listOf(
                BypassTask("library", "Сдать книги в библиотеку"),
                BypassTask("accounting", "Закрыть вопросы с бухгалтерией")
            )
        }
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i) ?: continue
                    val id = obj.optString("id", "")
                    val title = obj.optString("title", "")
                    val done = obj.optBoolean("done", false)
                    val note = obj.optString("note", "")
                    add(BypassTask(id = id, title = title, done = done, note = note))
                }
            }
        }.onFailure { Log.e(TAG, "loadTasks parse failed", it) }
            .getOrDefault(emptyList())
            .filter { it.id.isNotBlank() && it.title.isNotBlank() }
            .distinctBy { it.id }
    }

    private fun saveTasks() {
        val safe = _tasks.value
            .filter { it.id.isNotBlank() && it.title.isNotBlank() }
            .distinctBy { it.id }

        _tasks.value = safe

        val encoded = JSONArray().apply {
            safe.forEach { task ->
                put(
                    JSONObject().apply {
                        put("id", task.id)
                        put("title", task.title)
                        put("done", task.done)
                        put("note", task.note)
                    }
                )
            }
        }.toString()
        prefs.edit { putString(key, encoded) }
    }
}
