package com.example.week3_assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val uiState by viewModel.uiState.collectAsState()

                HomeScreen(
                    uiState = uiState,
                    onSearchQueryChange = viewModel::onSearchQueryChanged,
                    onRoomSelect = viewModel::onRoomSelected,
                    onToggleDevice = viewModel::toggleDeviceStatus
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onRoomSelect: (String) -> Unit,
    onToggleDevice: (Int) -> Unit
) {
    val rooms = listOf("Tümü", "Salon", "Mutfak", "Yatak Odası", "Bahçe", "Antre", "Koridor", "Balkon", "Ofis")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Aktif Toplam Tüketim", fontSize = 16.sp)
                Text(
                    text = "${String.format(Locale.getDefault(), "%.1f", uiState.totalEnergyConsumption)} W",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Cihaz Ara...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(rooms) { room ->
                FilterChip(
                    selected = uiState.selectedRoom == room,
                    onClick = { onRoomSelect(room) },
                    label = { Text(room) }
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(uiState.devices, key = { item -> item.id }) { device ->
                DeviceItem(device = device, onToggle = { onToggleDevice(device.id) })
            }
        }
    }
}

@Composable
fun DeviceItem(device: SmartDevice, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = device.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = "${device.room} • ${device.powerConsumption} W",
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 14.sp
                )
                Text(
                    text = if (device.isOnline) "Çevrimiçi" else "Çevrimdışı",
                    color = if (device.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Switch(
                checked = device.isActive,
                onCheckedChange = { onToggle() },
                enabled = device.isOnline
            )
        }
    }
}