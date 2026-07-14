package dev.stefano.enuventory.ui.screen.approval

import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import dev.stefano.enuventory.domain.usecase.ApproveRequestUseCase
import dev.stefano.enuventory.domain.usecase.GetPendingRequestsUseCase
import dev.stefano.enuventory.domain.usecase.RejectRequestUseCase
import dev.stefano.enuventory.fake.FakeBorrowRepository
import dev.stefano.enuventory.fake.MainDispatcherRule
import dev.stefano.enuventory.ui.common.UiState
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Menguji sisi admin dari langkah 3 di diagram alur peminjaman:
 * "Admin dapat notifikasi, ACC bisa datang ke kantor".
 *
 * Catatan: "dapat notifikasi" di sini cuma berarti realtime listener Firestore
 * yang aktif selama halaman Approval terbuka -- BUKAN push notification asli.
 * Lihat dokumen gap untuk detail.
 */
class ApprovalViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var borrowRepository: FakeBorrowRepository

    private fun createViewModel() = ApprovalViewModel(
        getPendingRequestsUseCase = GetPendingRequestsUseCase(borrowRepository),
        approveRequestUseCase = ApproveRequestUseCase(borrowRepository),
        rejectRequestUseCase = RejectRequestUseCase(borrowRepository)
    )

    @Before
    fun setUp() {
        borrowRepository = FakeBorrowRepository()
    }

    @Test
    fun `empty pending list shows Empty state`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel()
        val job = launch { viewModel.requestsState.collect {} }

        assertEquals(UiState.Empty, viewModel.requestsState.value)

        job.cancel()
    }

    @Test
    fun `only Pending records show up in the approval list`() =
        runTest(mainDispatcherRule.testDispatcher) {
            borrowRepository.setRecords(
                listOf(
                    record(id = "r1", status = BorrowStatus.Pending),
                    record(id = "r2", status = BorrowStatus.Borrowed),
                    record(id = "r3", status = BorrowStatus.Pending),
                    record(id = "r4", status = BorrowStatus.Rejected)
                )
            )
            val viewModel = createViewModel()
            val job = launch { viewModel.requestsState.collect {} }

            val state = viewModel.requestsState.value as UiState.Success
            assertEquals(setOf("r1", "r3"), state.data.map { it.id }.toSet())

            job.cancel()
        }

    @Test
    fun `approveRequest moves a record out of the pending list`() =
        runTest(mainDispatcherRule.testDispatcher) {
            borrowRepository.setRecords(listOf(record(id = "r1", status = BorrowStatus.Pending)))
            val viewModel = createViewModel()
            val job = launch { viewModel.requestsState.collect {} }

            viewModel.approveRequest("r1")

            assertEquals(BorrowStatus.Borrowed, borrowRepository.currentRecords().first().status)
            assertTrue(viewModel.requestsState.value is UiState.Empty)

            job.cancel()
        }

    @Test
    fun `rejectRequest moves a record out of the pending list`() =
        runTest(mainDispatcherRule.testDispatcher) {
            borrowRepository.setRecords(listOf(record(id = "r1", status = BorrowStatus.Pending)))
            val viewModel = createViewModel()
            val job = launch { viewModel.requestsState.collect {} }

            viewModel.rejectRequest("r1")

            assertEquals(BorrowStatus.Rejected, borrowRepository.currentRecords().first().status)
            assertTrue(viewModel.requestsState.value is UiState.Empty)

            job.cancel()
        }

    private fun record(id: String, status: BorrowStatus) = BorrowRecord(
        id = id,
        assetId = "asset-1",
        assetTitle = "Proyektor Epson",
        assetStock = 2,
        borrowerId = "user-1",
        borrowerName = "Budi",
        status = status,
        borrowDate = "14 Jul 2026, 09:00",
        returnEstimate = "20 Jul 2026"
    )
}
