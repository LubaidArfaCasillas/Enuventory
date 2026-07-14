package dev.stefano.enuventory.ui.screen.approval

import androidx.lifecycle.SavedStateHandle
import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import dev.stefano.enuventory.domain.usecase.ApproveRequestUseCase
import dev.stefano.enuventory.domain.usecase.GetBorrowRecordByIdUseCase
import dev.stefano.enuventory.domain.usecase.RejectRequestUseCase
import dev.stefano.enuventory.fake.FakeBorrowRepository
import dev.stefano.enuventory.fake.MainDispatcherRule
import dev.stefano.enuventory.ui.common.UiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Menguji langkah 3 di diagram: admin membuka detail request lalu ACC/tolak. */
class DetailRequestViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var borrowRepository: FakeBorrowRepository

    private val pendingRecord = BorrowRecord(
        id = "record-1",
        assetId = "asset-1",
        assetTitle = "Proyektor Epson",
        assetStock = 2,
        borrowerId = "user-1",
        borrowerName = "Budi",
        status = BorrowStatus.Pending,
        borrowDate = "14 Jul 2026, 09:00",
        returnEstimate = "20 Jul 2026"
    )

    private fun createViewModel(recordId: String = pendingRecord.id) = DetailRequestViewModel(
        savedStateHandle = SavedStateHandle(mapOf("recordId" to recordId)),
        getBorrowRecordByIdUseCase = GetBorrowRecordByIdUseCase(borrowRepository),
        approveRequestUseCase = ApproveRequestUseCase(borrowRepository),
        rejectRequestUseCase = RejectRequestUseCase(borrowRepository)
    )

    @Before
    fun setUp() {
        borrowRepository = FakeBorrowRepository().apply { setRecords(listOf(pendingRecord)) }
    }

    @Test
    fun `loadRecord shows Success with the requested record`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            val state = viewModel.uiState.value as UiState.Success
            assertEquals(pendingRecord.id, state.data.id)
        }

    @Test
    fun `loadRecord shows Error when record does not exist`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel(recordId = "does-not-exist")
            assertTrue(viewModel.uiState.value is UiState.Error)
        }

    @Test
    fun `approveRequest sets status to Borrowed and calls onSuccess`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            var onSuccessCalled = false

            viewModel.approveRequest(onSuccess = { onSuccessCalled = true })

            assertTrue(onSuccessCalled)
            assertEquals(BorrowStatus.Borrowed, borrowRepository.currentRecords().first().status)
        }

    @Test
    fun `rejectRequest sets status to Rejected and calls onSuccess`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            var onSuccessCalled = false

            viewModel.rejectRequest(onSuccess = { onSuccessCalled = true })

            assertTrue(onSuccessCalled)
            assertEquals(BorrowStatus.Rejected, borrowRepository.currentRecords().first().status)
        }

    @Test
    fun `approveRequest failure surfaces an Error state and skips onSuccess`() =
        runTest(mainDispatcherRule.testDispatcher) {
            borrowRepository.approveRequestError = IllegalStateException("Firestore down")
            val viewModel = createViewModel()
            var onSuccessCalled = false

            viewModel.approveRequest(onSuccess = { onSuccessCalled = true })

            assertFalse(onSuccessCalled)
            assertTrue(viewModel.uiState.value is UiState.Error)
        }
}
