package dev.stefano.enuventory.ui.screen.notification

import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import dev.stefano.enuventory.domain.usecase.GetPendingRequestsUseCase
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

/** Menguji [AdminNotificationViewModel]: notifikasi in-app dari request pending. */
class AdminNotificationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var borrowRepository: FakeBorrowRepository

    private fun createViewModel() = AdminNotificationViewModel(
        getPendingRequestsUseCase = GetPendingRequestsUseCase(borrowRepository)
    )

    @Before
    fun setUp() {
        borrowRepository = FakeBorrowRepository()
    }

    @Test
    fun `notificationsState is Empty when there are no pending requests`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            val job = launch { viewModel.notificationsState.collect {} }

            assertEquals(UiState.Empty, viewModel.notificationsState.value)

            job.cancel()
        }

    @Test
    fun `notificationsState maps each pending request to a notification item`() =
        runTest(mainDispatcherRule.testDispatcher) {
            borrowRepository.setRecords(
                listOf(
                    BorrowRecord(
                        id = "r1",
                        assetId = "HW-1",
                        assetTitle = "Macbook Pro 14",
                        assetStock = 1,
                        borrowerId = "u1",
                        borrowerName = "Budi Santoso",
                        status = BorrowStatus.Pending,
                        borrowDate = "1 Jan",
                        returnEstimate = "8 Jan"
                    )
                )
            )
            val viewModel = createViewModel()
            val job = launch { viewModel.notificationsState.collect {} }

            val state = viewModel.notificationsState.value as UiState.Success
            assertEquals(1, state.data.size)
            val item = state.data.first()
            assertEquals("r1", item.id)
            assertTrue(item.message.contains("Budi Santoso"))
            assertTrue(item.message.contains("Macbook Pro 14"))

            job.cancel()
        }
}
