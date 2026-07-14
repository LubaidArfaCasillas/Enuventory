package dev.stefano.enuventory.ui.screen.home

import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import dev.stefano.enuventory.domain.model.Category
import dev.stefano.enuventory.domain.model.User
import dev.stefano.enuventory.domain.model.UserRole
import dev.stefano.enuventory.domain.usecase.GetAssetsUseCase
import dev.stefano.enuventory.domain.usecase.GetCategoriesUseCase
import dev.stefano.enuventory.domain.usecase.GetCurrentUserUseCase
import dev.stefano.enuventory.domain.usecase.GetPendingRequestsUseCase
import dev.stefano.enuventory.domain.usecase.GetUserBorrowHistoryUseCase
import dev.stefano.enuventory.fake.FakeAssetRepository
import dev.stefano.enuventory.fake.FakeAuthRepository
import dev.stefano.enuventory.fake.FakeBorrowRepository
import dev.stefano.enuventory.fake.FakeCategoryRepository
import dev.stefano.enuventory.fake.MainDispatcherRule
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Menguji [HomeViewModel]: sinkronisasi badge kategori & badge notifikasi (Admin/User). */
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var assetRepository: FakeAssetRepository
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var borrowRepository: FakeBorrowRepository
    private lateinit var authRepository: FakeAuthRepository

    private val currentUser =
        User(uid = "u1", name = "Budi", email = "budi@x.com", role = UserRole.RegularUser)

    private fun createViewModel() = HomeViewModel(
        getAssetsUseCase = GetAssetsUseCase(assetRepository),
        getCategoriesUseCase = GetCategoriesUseCase(categoryRepository),
        getPendingRequestsUseCase = GetPendingRequestsUseCase(borrowRepository),
        getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
        getUserBorrowHistoryUseCase = GetUserBorrowHistoryUseCase(borrowRepository)
    )

    private fun formatDate(date: Date): String =
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date)

    @Before
    fun setUp() {
        assetRepository = FakeAssetRepository()
        categoryRepository = FakeCategoryRepository()
        borrowRepository = FakeBorrowRepository()
        authRepository = FakeAuthRepository(initialUser = currentUser)
    }

    @Test
    fun `categoriesState defaults to just All when there are no categories yet`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            val job = launch { viewModel.categoriesState.collect {} }

            assertEquals(listOf("All"), viewModel.categoriesState.value)

            job.cancel()
        }

    @Test
    fun `categoriesState prefixes All before the real managed categories`() =
        runTest(mainDispatcherRule.testDispatcher) {
            categoryRepository.setCategories(
                listOf(
                    Category(id = "1", name = "Elektro"),
                    Category(id = "2", name = "IoT"),
                    Category(id = "3", name = "Mekanik")
                )
            )
            val viewModel = createViewModel()
            val job = launch { viewModel.categoriesState.collect {} }

            assertEquals(
                listOf("All", "Elektro", "IoT", "Mekanik"),
                viewModel.categoriesState.value
            )

            job.cancel()
        }

    @Test
    fun `adminNotificationCount reflects the number of pending requests`() =
        runTest(mainDispatcherRule.testDispatcher) {
            borrowRepository.setRecords(
                listOf(
                    BorrowRecord(
                        id = "r1",
                        assetId = "HW-1",
                        assetTitle = "Laptop",
                        assetStock = 1,
                        borrowerId = "u1",
                        borrowerName = "Budi",
                        status = BorrowStatus.Pending,
                        borrowDate = "1 Jan",
                        returnEstimate = "8 Jan"
                    ),
                    BorrowRecord(
                        id = "r2",
                        assetId = "HW-2",
                        assetTitle = "Mouse",
                        assetStock = 1,
                        borrowerId = "u1",
                        borrowerName = "Budi",
                        status = BorrowStatus.Pending,
                        borrowDate = "1 Jan",
                        returnEstimate = "8 Jan"
                    ),
                    BorrowRecord(
                        id = "r3",
                        assetId = "HW-3",
                        assetTitle = "Keyboard",
                        assetStock = 1,
                        borrowerId = "u1",
                        borrowerName = "Budi",
                        status = BorrowStatus.Borrowed,
                        borrowDate = "1 Jan",
                        returnEstimate = "8 Jan"
                    )
                )
            )
            val viewModel = createViewModel()
            val job = launch { viewModel.adminNotificationCount.collect {} }

            assertEquals(2, viewModel.adminNotificationCount.value)

            job.cancel()
        }

    @Test
    fun `userNotificationCount counts only that user's near-deadline borrowed records`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val now = Date()
            val soon = Date(now.time + 60 * 60 * 1000) // 1 jam lagi
            val far = Date(now.time + 10L * 24 * 60 * 60 * 1000) // 10 hari lagi
            borrowRepository.setRecords(
                listOf(
                    BorrowRecord(
                        id = "r1",
                        assetId = "HW-1",
                        assetTitle = "Laptop",
                        assetStock = 1,
                        borrowerId = "u1",
                        borrowerName = "Budi",
                        status = BorrowStatus.Borrowed,
                        borrowDate = "1 Jan",
                        returnEstimate = formatDate(soon)
                    ),
                    BorrowRecord(
                        id = "r2",
                        assetId = "HW-2",
                        assetTitle = "Mouse",
                        assetStock = 1,
                        borrowerId = "u1",
                        borrowerName = "Budi",
                        status = BorrowStatus.Borrowed,
                        borrowDate = "1 Jan",
                        returnEstimate = formatDate(far)
                    ),
                    BorrowRecord(
                        id = "r3",
                        assetId = "HW-3",
                        assetTitle = "Keyboard",
                        assetStock = 1,
                        borrowerId = "someone-else",
                        borrowerName = "Lain",
                        status = BorrowStatus.Borrowed,
                        borrowDate = "1 Jan",
                        returnEstimate = formatDate(soon)
                    )
                )
            )
            val viewModel = createViewModel()
            val job = launch { viewModel.userNotificationCount.collect {} }

            assertEquals(1, viewModel.userNotificationCount.value)

            job.cancel()
        }
}
