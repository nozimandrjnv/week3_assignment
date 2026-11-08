package com.example.week3_assignment

data class HomeUiState(
    val searchQuery: String = "",
    val selectedRoom: String = "Tümü",
    val devices: List<SmartDevice> = emptyList(),
    val totalEnergyConsumption: Double = 0.0
)