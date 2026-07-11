package dev.stefano.enuventory.data

import dev.stefano.enuventory.ui.components.EnuInventoryStatus

data class InventoryItem(
    val id: String,
    val title: String,
    val stock: Int,
    val status: EnuInventoryStatus,
    val category: String
)

val dummyInventoryItems = listOf(
    InventoryItem(
        "HW-0019-A",
        "Arduino Micro Controller",
        5,
        EnuInventoryStatus.Tersedia,
        "Elektro"
    ),
    InventoryItem(
        "HW-0020-B",
        "Raspberry Pi 4 Model B",
        2,
        EnuInventoryStatus.TidakTersedia,
        "IoT"
    ),
    InventoryItem(
        "HW-0021-C",
        "Sensor Ultrasonik HC-SR04",
        12,
        EnuInventoryStatus.Maintenance,
        "Elektro"
    )
)