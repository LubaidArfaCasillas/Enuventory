package dev.stefano.enuventory.fake

import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import dev.stefano.enuventory.domain.repository.BorrowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory fake untuk [BorrowRepository], dipakai di unit test ViewModel peminjaman
 * & pengembalian. Status di-derive murni dari data, sama seperti Firestore asli.
 */
class FakeBorrowRepository : BorrowRepository {

    private val recordsFlow = MutableStateFlow<List<BorrowRecord>>(emptyList())
    private var nextId = 1

    var requestBorrowError: Throwable? = null
    var approveRequestError: Throwable? = null
    var rejectRequestError: Throwable? = null
    var returnAssetError: Throwable? = null

    val returnProofUrls = mutableListOf<String?>()

    fun setRecords(records: List<BorrowRecord>) {
        recordsFlow.value = records
    }

    fun currentRecords(): List<BorrowRecord> = recordsFlow.value

    override fun getBorrowRecordsByUser(userId: String): Flow<List<BorrowRecord>> =
        recordsFlow.map { list -> list.filter { it.borrowerId == userId } }

    override fun getPendingRequests(): Flow<List<BorrowRecord>> =
        recordsFlow.map { list -> list.filter { it.status == BorrowStatus.Pending } }

    override suspend fun getBorrowRecordById(recordId: String): BorrowRecord? =
        recordsFlow.value.find { it.id == recordId }

    override suspend fun requestBorrow(
        assetId: String,
        assetTitle: String,
        assetStock: Int,
        userId: String,
        userName: String,
        returnEstimate: String
    ) {
        requestBorrowError?.let { throw it }
        val record = BorrowRecord(
            id = "record-${nextId++}",
            assetId = assetId,
            assetTitle = assetTitle,
            assetStock = assetStock,
            borrowerId = userId,
            borrowerName = userName,
            status = BorrowStatus.Pending,
            borrowDate = "14 Jul 2026, 10:00",
            returnEstimate = returnEstimate
        )
        recordsFlow.value = recordsFlow.value + record
    }

    override suspend fun approveRequest(recordId: String) {
        approveRequestError?.let { throw it }
        updateStatus(recordId, BorrowStatus.Borrowed)
    }

    override suspend fun rejectRequest(recordId: String) {
        rejectRequestError?.let { throw it }
        updateStatus(recordId, BorrowStatus.Rejected)
    }

    override suspend fun returnAsset(recordId: String, proofImageUrl: String?) {
        returnAssetError?.let { throw it }
        returnProofUrls += proofImageUrl
        recordsFlow.value = recordsFlow.value.map {
            if (it.id == recordId) {
                it.copy(
                    status = BorrowStatus.Completed,
                    returnDate = "14 Jul 2026, 12:00",
                    proofImageUrl = proofImageUrl
                )
            } else {
                it
            }
        }
    }

    private fun updateStatus(recordId: String, status: BorrowStatus) {
        recordsFlow.value = recordsFlow.value.map {
            if (it.id == recordId) it.copy(status = status) else it
        }
    }
}
