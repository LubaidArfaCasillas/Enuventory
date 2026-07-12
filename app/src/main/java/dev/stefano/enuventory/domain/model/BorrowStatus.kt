package dev.stefano.enuventory.domain.model

/**
 * Status sebuah peminjaman/request.
 * Ini adalah domain enum — tidak bergantung pada layer UI manapun.
 */
enum class BorrowStatus {
    Pending,
    Borrowed,
    Rejected,
    Completed
}
