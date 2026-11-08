package com.example.week3_assignment

data class SmartDevice(
    val id: Int,
    val name: String,
    val room: String,
    val category: String,
    val isOnline: Boolean,
    val isActive: Boolean,
    val powerConsumption: Double
)