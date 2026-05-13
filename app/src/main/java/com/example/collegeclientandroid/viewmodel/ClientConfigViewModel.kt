package com.example.collegeclientandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeclientandroid.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientAppUiState(
    val maintenanceEnabled: Boolean = false,
    val maintenanceMessage: String = ""
)

@HiltViewModel
class ClientConfigViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientAppUiState())
    val uiState: StateFlow<ClientAppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                refresh()
                delay(30_000L)
            }
        }
    }

    suspend fun refresh() {
        try {
            val r = api.getClientAppConfig()
            if (r.isSuccessful && r.body() != null) {
                val b = r.body()!!
                _uiState.value = ClientAppUiState(
                    maintenanceEnabled = b.maintenanceEnabled,
                    maintenanceMessage = b.maintenanceMessage.orEmpty()
                )
            }
        } catch (_: Exception) {
            // сеть недоступна — не включаем техработы
        }
    }
}
