package dev.stefano.enuventory.ui.screen.history

import androidx.lifecycle.SavedStateHandle
import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import dev.stefano.enuventory.domain.usecase.GetBorrowRecordByIdUseCase
import dev.stefano.enuventory.fake.FakeBorrowRepository
import dev.stefano.enuventory.fake.MainDispatcherRule
import dev.stefano.enuventory.ui.common.UiState
import dev.stefano.enuventory.ui.pages.DetailRiwayatState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Menguji derivasi status di halaman Detail Riwayat -- ini titik pertemuan alur
 * peminjaman & pengembalian di diagram (dari sini user pencet "Scan QR" atau
 * "Kembalikan" tergantung state).
 *
 * Catatan: `BorrowStatus.Rejected` -> `DetailRiwayatState.Ditolak` sempat jadi bug
 * (ke-mapping sama seperti `Pending`, sudah diperbaiki -- lihat riwayat `workflow-gaps.md`).
 *
 * `DetailRiwayatState.MenungguPengambilan` (state "Menunggu Pengambilan" + tombol Scan QR)
 * masih TIDAK PERNAH diproduksi oleh ViewModel ini, karena `BorrowStatus.Borrowed` langsung
 * dipetakan ke `BatasKembali` -- gap ini masih terdaftar di `docs/workflow-gaps.md` gap #2.
 */
class DetailRiwayatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var borrowRepository: FakeBorrowRepository

    private fun createViewModel(recordId: String = "record-1") = DetailRiwayatViewModel(
        savedStateHandle = SavedStateHandle(mapOf("recordId" to recordId)),
        getBorrowRecordByIdUseCase = GetBorrowRecordByIdUseCase(borrowRepository)
    )

    private fun recordWith(status: BorrowStatus) = BorrowRecord(
        id = "record-1",
        assetId = "asset-1",
        assetTitle = "Proyektor Epson",
        assetStock = 2,
        borrowerId = "user-1",
        borrowerName = "Budi",
        status = status,
        borrowDate = "14 Jul 2026, 09:00",
        returnEstimate = "20 Jul 2026"
    )

    @Before
    fun setUp() {
        borrowRepository = FakeBorrowRepository()
    }

    @Test
    fun `Pending status shows MenungguPersetujuan`() = runTest(mainDispatcherRule.testDispatcher) {
        borrowRepository.setRecords(listOf(recordWith(BorrowStatus.Pending)))
        val viewModel = createViewModel()

        val state = viewModel.uiState.value as UiState.Success
        assertEquals(DetailRiwayatState.MenungguPersetujuan, state.data.riwayatState)
    }

    @Test
    fun `Borrowed status shows BatasKembali, skipping the pickup-QR step (see class doc, gap #2)`() =
        runTest(mainDispatcherRule.testDispatcher) {
            borrowRepository.setRecords(listOf(recordWith(BorrowStatus.Borrowed)))
            val viewModel = createViewModel()

            val state = viewModel.uiState.value as UiState.Success
            assertEquals(DetailRiwayatState.BatasKembali, state.data.riwayatState)
        }

    @Test
    fun `Completed status shows Dikembalikan`() = runTest(mainDispatcherRule.testDispatcher) {
        borrowRepository.setRecords(listOf(recordWith(BorrowStatus.Completed)))
        val viewModel = createViewModel()

        val state = viewModel.uiState.value as UiState.Success
        assertEquals(DetailRiwayatState.Dikembalikan, state.data.riwayatState)
    }

    @Test
    fun `Rejected status shows Ditolak`() =
        runTest(mainDispatcherRule.testDispatcher) {
            borrowRepository.setRecords(listOf(recordWith(BorrowStatus.Rejected)))
            val viewModel = createViewModel()

            val state = viewModel.uiState.value as UiState.Success
            assertEquals(DetailRiwayatState.Ditolak, state.data.riwayatState)
        }

    @Test
    fun `record not found shows Error`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel(recordId = "does-not-exist")
        assertTrue(viewModel.uiState.value is UiState.Error)
    }
}
