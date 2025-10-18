package com.example.collegeclientandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeclientandroid.ApiService
import com.example.collegeclientandroid.HomeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val apiService: ApiService
): ViewModel() {

    private val _screenState: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState())
    val screenState: StateFlow<HomeScreenState> = _screenState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
    
    private fun getDayOfWeek(dateMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Понедельник"
            Calendar.TUESDAY -> "Вторник"
            Calendar.WEDNESDAY -> "Среда"
            Calendar.THURSDAY -> "Четверг"
            Calendar.FRIDAY -> "Пятница"
            Calendar.SATURDAY -> "Суббота"
            Calendar.SUNDAY -> "воскресенье"
            else -> "неизвестно"
        }
    }

    fun onDatePickerClicked() {
        _screenState.value = _screenState.value.copy(showDatePicker = true)
    }

    fun onDatePickerDismiss() {
        _screenState.value = _screenState.value.copy(showDatePicker = false)
    }

    fun onDateSelected(dateMillis: Long) {
        val selectedDate = dateFormatter.format(Date(dateMillis))
        _screenState.value = _screenState.value.copy(
            showDatePicker = false,
            selectedDate = selectedDate,
            selectedDateMillis = dateMillis,
            errorMessage = null
        )
    }

    fun onGroupNameChanged(newGroup: String) {
        _screenState.value = _screenState.value.copy(
            groupName = newGroup,
            errorMessage = null
        )
    }

    fun onProfileClicked() {
        // Навигация в профиль
    }


    fun setError(message: String) {
        _screenState.value = _screenState.value.copy(errorMessage = message)
    }

    fun loadSchedule(groupName: String, dateMillis: Long) {
        if (groupName.isBlank()) {
            setError("Введите номер группы")
            return
        }
        
        _screenState.value = _screenState.value.copy(isLoading = true, errorMessage = null)
        
        val dayOfWeek = getDayOfWeek(dateMillis)
        
        viewModelScope.launch {
            try {
                val schedule = apiService.getSchedule(groupName, dayOfWeek)
                _screenState.value = _screenState.value.copy(
                    isLoading = false,
                    schedule = schedule,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _screenState.value = _screenState.value.copy(
                    isLoading = false,
                    schedule = emptyList(),
                    errorMessage = "Ошибка загрузки расписания: ${e.message}"
                )
            }
        }
    }

    fun clearSchedule() {
        _screenState.value = _screenState.value.copy(schedule = emptyList())
    }
}
