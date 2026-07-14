package dev.stefano.enuventory.ui.screen.notification

import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import dev.stefano.enuventory.domain.model.User
import dev.stefano.enuventory.domain.model.UserRole
import dev.stefano.enuventory.domain.usecase.GetCurrentUserUseCase
import dev.stefano.enuventory.domain.usecase.GetUserBorrowHistoryUseCase
import dev.stefano.enuventory.fake.FakeAuthRepository
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Menguji [UserNotificationViewModel]: notifikasi in-app buat batas kembali yang mendekat. */
class UserNotificationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var borrowRepository: FakeBorrowRepository
    private lateinit var authRepository: FakeAuthRepository

    private val currentUser =
        User(uid = "u1", name = "Budi", email = "budi@x.com", role = UserRole.RegularUser)

    private fun createViewModel() = UserNotificationViewModel(
        getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
        getUserBorrowHistoryUseCase = GetUserBorrowHistoryUseCase(borrowRepository)
    )

    private fun formatDate(date: Date): String =
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date)

    @Before
    fun setUp() {
        borrowRepository = FakeBorrowRepository()
        authRepository = FakeAuthRepository(initialUser = currentUser)
    }

    @Test
    fun `notificationsState is Empty when nothing is near its deadline`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            val job = launch { viewModel.notificationsState.collect {} }

            assertEquals(UiState.Empty, viewModel.notificationsState.value)

            job.cancel()
        }

    @Test
    fun `notificationsState surfaces a record whose deadline is near`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val soon = Date(System.currentTimeMillis() + 60 * 60 * 1000)
            borrowRepository.setRecords(
                listOf(
                    BorrowRecord(
                        id = "r1",
                        assetId = "HW-1",
                        assetTitle = "Macbook Pro 14",
                        assetStock = 1,
                        borrowerId = "u1",
                        borrowerName = "Budi",
                        status = BorrowStatus.Borrowed,
                        borrowDate = "1 Jan",
                        returnEstimate = formatDate(soon)
                    )
                )
            )
            val viewModel = createViewModel()
            val job = launch { viewModel.notificationsState.collect {} }

            val state = viewModel.notificationsState.value as UiState.Success
            assertEquals(1, state.data.size)
            assertTrue(state.data.first().message.contains("Macbook Pro 14"))

            job.cancel()
        }
}
