package com.example.week3_assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val initialDevices = listOf(
        SmartDevice(1, "Köşe Lambası", "Salon", "LIGHT", isOnline = true, isActive = true, powerConsumption = 15.0),
        SmartDevice(2, "Tavan Avizesi", "Salon", "LIGHT", isOnline = true, isActive = false, powerConsumption = 60.0),
        SmartDevice(3, "Bahçe Kamerası", "Bahçe", "CAMERA", isOnline = true, isActive = true, powerConsumption = 8.5),
        SmartDevice(4, "Giriş Kamerası", "Antre", "CAMERA", isOnline = true, isActive = true, powerConsumption = 8.5),
        SmartDevice(5, "Merkezi Termostat", "Salon", "THERMOSTAT", isOnline = true, isActive = true, powerConsumption = 12.0),
        SmartDevice(6, "Yatak Odası Kliması", "Yatak Odası", "THERMOSTAT", isOnline = true, isActive = false, powerConsumption = 1200.0),
        SmartDevice(7, "Mutfak Spotları", "Mutfak", "LIGHT", isOnline = true, isActive = false, powerConsumption = 45.0),
        SmartDevice(8, "Roborock S7", "Koridor", "VACUUM", isOnline = true, isActive = true, powerConsumption = 50.0),
        SmartDevice(9, "Balkon Aydınlatması", "Balkon", "LIGHT", isOnline = false, isActive = false, powerConsumption = 25.0),
        SmartDevice(10, "Çalışma Odası Lambası", "Ofis", "LIGHT", isOnline = true, isActive = true, powerConsumption = 12.0)
    )

    private val _allDevices = MutableStateFlow(initialDevices)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        updateState()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        updateState()
    }

    fun onRoomSelected(room: String) {
        _uiState.update { it.copy(selectedRoom = room) }
        updateState()
    }

    fun toggleDeviceStatus(deviceId: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            _allDevices.update { list ->
                list.map { device ->
                    if (device.id == deviceId) {
                        device.copy(isActive = !device.isActive)
                    } else {
                        device
                    }
                }
            }
            updateState()
        }
    }

    private fun updateState() {
        viewModelScope.launch(Dispatchers.Default) {
            val query = _uiState.value.searchQuery.lowercase().trim()
            val room = _uiState.value.selectedRoom

            val filteredList = _allDevices.value.filter { device ->
                val matchesQuery = device.name.lowercase().contains(query)
                val matchesRoom = if (room == "Tümü") true else device.room.equals(room, ignoreCase = true)
                matchesQuery && matchesRoom
            }

            val totalEnergy = _allDevices.value
                .filter { it.isOnline && it.isActive }
                .sumOf { it.powerConsumption }

            _uiState.update {
                it.copy(
                    devices = filteredList,
                    totalEnergyConsumption = totalEnergy
                )
            }
        }
    }
}