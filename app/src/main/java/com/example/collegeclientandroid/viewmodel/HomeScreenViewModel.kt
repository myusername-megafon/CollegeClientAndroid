package com.example.collegeclientandroid.viewmodel

import androidx.lifecycle.ViewModel
import com.example.collegeclientandroid.HomeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(): ViewModel() {

    private val _screenState: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState())
    val screenState: StateFlow<HomeScreenState> = _screenState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))

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

        val mockSchedule = listOf(
            "1пара: компьютерные сети Симонян 210",
            "2пара: компьютерные сети Симонян 210", 
            "3пара: ин.яз. Карловская Финина 219,306н"
        )
        
        _screenState.value = _screenState.value.copy(
            isLoading = false,
            schedule = mockSchedule
        )
    }

    fun clearSchedule() {
        _screenState.value = _screenState.value.copy(schedule = emptyList())
    }
}