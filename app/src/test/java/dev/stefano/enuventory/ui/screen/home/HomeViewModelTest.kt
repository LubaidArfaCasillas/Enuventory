package dev.stefano.enuventory.ui.screen.home

import dev.stefano.enuventory.domain.model.Category
import dev.stefano.enuventory.domain.usecase.GetAssetsUseCase
import dev.stefano.enuventory.domain.usecase.GetCategoriesUseCase
import dev.stefano.enuventory.fake.FakeAssetRepository
import dev.stefano.enuventory.fake.FakeCategoryRepository
import dev.stefano.enuventory.fake.MainDispatcherRule
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Menguji [HomeViewModel.categoriesState]: badge filter kategori Home harus sinkron dengan
 * kategori yang beneran dikelola lewat Kelola Kategori, bukan daftar hardcoded. */
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var assetRepository: FakeAssetRepository
    private lateinit var categoryRepository: FakeCategoryRepository

    private fun createViewModel() = HomeViewModel(
        getAssetsUseCase = GetAssetsUseCase(assetRepository),
        getCategoriesUseCase = GetCategoriesUseCase(categoryRepository)
    )

    @Before
    fun setUp() {
        assetRepository = FakeAssetRepository()
        categoryRepository = FakeCategoryRepository()
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
}
