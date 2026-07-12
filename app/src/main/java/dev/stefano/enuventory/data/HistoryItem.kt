package dev.stefano.enuventory.data

import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus

// TODO: File ini bersifat sementara untuk dummy data selama belum ada repository.
// Akan dihapus saat BorrowRepository sudah diimplementasikan.
val dummyBorrowRecords = listOf(
    // Data untuk Tab Aktif
    BorrowRecord(
        id = "REQ-001",
        assetId = "HW-0019-A",
        assetTitle = "Arduino Micro Controller",
        assetStock = 5,
        borrowerId = "USR-001",
        borrowerName = "Budi Santoso",
        status = BorrowStatus.Pending,
        borrowDate = "16 Okt 26",
        returnEstimate = "23 Okt 26"
    ),
    BorrowRecord(
        id = "REQ-002",
        assetId = "HW-0019-A",
        assetTitle = "Arduino Micro Controller",
        assetStock = 5,
        borrowerId = "USR-001",
        borrowerName = "Budi Santoso",
        status = BorrowStatus.Borrowed,
        borrowDate = "16 Okt 26",
        returnEstimate = "23 Okt 26"
    ),

    // Data untuk Tab Selesai
    BorrowRecord(
        id = "REQ-003",
        assetId = "HW-0019-A",
        assetTitle = "Arduino Micro Controller",
        assetStock = 5,
        borrowerId = "USR-002",
        borrowerName = "Ani Rahayu",
        status = BorrowStatus.Rejected,
        borrowDate = "16 Okt 26",
        returnEstimate = "-"
    ),
    BorrowRecord(
        id = "REQ-004",
        assetId = "HW-0019-A",
        assetTitle = "Arduino Micro Controller",
        assetStock = 5,
        borrowerId = "USR-002",
        borrowerName = "Ani Rahayu",
        status = BorrowStatus.Completed,
        borrowDate = "16 Okt 26",
        returnEstimate = "23 Okt 26",
        returnDate = "21 Okt 26"
    )
)