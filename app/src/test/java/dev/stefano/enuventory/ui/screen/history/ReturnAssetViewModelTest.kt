package dev.stefano.enuventory.ui.screen.history

import androidx.lifecycle.SavedStateHandle
import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import dev.stefano.enuventory.domain.usecase.GetBorrowRecordByIdUseCase
import dev.stefano.enuventory.domain.usecase.ReturnAssetUseCase
import dev.stefano.enuventory.fake.FakeBorrowRepository
import dev.stefano.enuventory.fake.MainDispatcherRule
import dev.stefano.enuventory.ui.common.UiState
import dev.stefano.enuventory.ui.pages.UploadBuktiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Menguji ALUR PENGEMBALIAN: "user taruh barang -> upload bukti foto -> selesai".
 *
 * Catatan: `submitReturn()` saat ini selalu mengirim URL foto dummy/hardcoded
 * (bukan hasil upload asli ke Firebase Storage). Test di bawah memverifikasi
 * proofUrl yang diteruskan ke repository memang string dummy tsb apa adanya --
 * lihat dokumen gap untuk detail & saran perbaikan.
 */
class ReturnAssetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var borrowRepository: FakeBorrowRepository

    private val borrowedRecord = BorrowRecord(
        id = "record-1",
        assetId = "asset-1",
        assetTitle = "Proyektor Epson",
        assetStock = 2,
        borrowerId = "user-1",
        borrowerName = "Budi",
        status = BorrowStatus.Borrowed,
        borrowDate = "14 Jul 2026, 09:00",
        returnEstimate = "20 Jul 2026"
    )

    private fun createViewModel(recordId: String = borrowedRecord.id) = ReturnAssetViewModel(
        savedStateHandle = SavedStateHandle(mapOf("recordId" to recordId)),
        getBorrowRecordByIdUseCase = GetBorrowRecordByIdUseCase(borrowRepository),
        returnAssetUseCase = ReturnAssetUseCase(borrowRepository)
    )

    @Before
    fun setUp() {
        borrowRepository = FakeBorrowRepository().apply { setRecords(listOf(borrowedRecord)) }
    }

    @Test
    fun `loadRecord loads the borrowed record`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel()
        val state = viewModel.recordState.value as UiState.Success
        assertEquals(borrowedRecord.id, state.data.id)
    }

    @Test
    fun `initial upload state is Capture`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel()
        assertEquals(UploadBuktiState.Capture, viewModel.uploadState.value)
    }

    @Test
    fun `onCapturePhoto moves to PreviewImage, onUlangiCapture moves back to Capture`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()

            viewModel.onCapturePhoto()
            assertEquals(UploadBuktiState.PreviewImage, viewModel.uploadState.value)

            viewModel.onUlangiCapture()
            assertEquals(UploadBuktiState.Capture, viewModel.uploadState.value)
        }

    @Test
    fun `submitReturn marks the record Completed and calls onSuccess`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            var onSuccessCalled = false

            viewModel.submitReturn(onSuccess = { onSuccessCalled = true })

            assertTrue(onSuccessCalled)
            val updated = borrowRepository.currentRecords().first()
            assertEquals(BorrowStatus.Completed, updated.status)
            assertEquals(1, borrowRepository.returnProofUrls.size)
            assertTrue(
                "proof url should be non-blank (currently a dummy placeholder, see gap doc)",
                borrowRepository.returnProofUrls.first()?.isNotBlank() == true
            )
        }

    @Test
    fun `submitReturn failure surfaces Error upload state and skips onSuccess`() =
        runTest(mainDispatcherRule.testDispatcher) {
            borrowRepository.returnAssetError = IllegalStateException("Storage down")
            val viewModel = createViewModel()
            var onSuccessCalled = false

            viewModel.submitReturn(onSuccess = { onSuccessCalled = true })

            assertEquals(false, onSuccessCalled)
            assertEquals(UploadBuktiState.Error, viewModel.uploadState.value)
        }
}
