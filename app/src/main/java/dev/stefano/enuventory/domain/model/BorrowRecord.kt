package dev.stefano.enuventory.domain.model

/**
 * Domain entity untuk sebuah record peminjaman.
 *
 * Ini adalah representasi "murni" dari data bisnis — tidak ada
 * dependency ke Android framework, Room entity, maupun Firestore DTO.
 */
data class BorrowRecord(
    val id: String,
    val assetId: String,
    val assetTitle: String,
    val assetStock: Int,
    val borrowerId: String,
    val borrowerName: String,
    val status: BorrowStatus,
    val borrowDate: String,
    val returnEstimate: String,
    val returnDate: String? = null,
    val proofImageUrl: String? = null
) {
    /** Helper property — true jika request sudah selesai (ditolak atau dikembalikan). */
    val isFinished: Boolean
        get() = status == BorrowStatus.Rejected || status == BorrowStatus.Completed
}
