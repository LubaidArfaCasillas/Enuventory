package dev.stefano.enuventory.ui.screen.asset

import dev.stefano.enuventory.domain.model.AssetStatus
import dev.stefano.enuventory.domain.usecase.AddAssetUseCase
import dev.stefano.enuventory.domain.usecase.GetAssetByIdUseCase
import dev.stefano.enuventory.fake.FakeAssetRepository
import dev.stefano.enuventory.fake.MainDispatcherRule
import dev.stefano.enuventory.ui.common.UiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Menguji [TambahAssetViewModel], khususnya perbaikan bug ID collision: generator ID lama
 * (timestamp % 100000) bisa diam-diam nge-overwrite asset lain karena `addAsset()` pakai
 * `.set()`. Sekarang ID di-generate acak lalu dicek dulu ke repository sebelum dipakai.
 */
class TambahAssetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var assetRepository: FakeAssetRepository

    private fun createViewModel() = TambahAssetViewModel(
        addAssetUseCase = AddAssetUseCase(assetRepository),
        getAssetByIdUseCase = GetAssetByIdUseCase(assetRepository)
    )

    @Before
    fun setUp() {
        assetRepository = FakeAssetRepository()
    }

    @Test
    fun `addAsset succeeds and generated id follows the HW- format`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            var onSuccessCalled = false

            viewModel.addAsset(
                title = "Proyektor Epson",
                stockStr = "3",
                statusStr = "Tersedia",
                category = "Elektronik",
                description = "Proyektor buat presentasi",
                onSuccess = { onSuccessCalled = true }
            )

            assertTrue(onSuccessCalled)
            assertEquals(UiState.Success(Unit), viewModel.addState.value)
            assertEquals(1, assetRepository.addAssetCalls.size)
            val added = assetRepository.addAssetCalls.first()
            assertTrue(Regex("^HW-[23456789ABCDEFGHJKMNPQRSTVWXYZ]{5}$").matches(added.id))
            assertEquals(AssetStatus.Available, added.status)
            assertEquals(3, added.stock)
        }

    @Test
    fun `addAsset retries id generation when the first candidate id is already taken`() =
        runTest(mainDispatcherRule.testDispatcher) {
            assetRepository.collideForNextNLookups =
                2 // 2 collision dulu, baru berhasil di attempt ke-3
            val viewModel = createViewModel()
            var onSuccessCalled = false

            viewModel.addAsset(
                title = "Solder",
                stockStr = "1",
                statusStr = "Tersedia",
                category = "Perkakas",
                description = "",
                onSuccess = { onSuccessCalled = true }
            )

            assertTrue(onSuccessCalled)
            assertEquals(1, assetRepository.addAssetCalls.size)
        }

    @Test
    fun `addAsset fails gracefully when id keeps colliding past the retry limit`() =
        runTest(mainDispatcherRule.testDispatcher) {
            assetRepository.collideForNextNLookups = 10 // lebih dari MAX_ID_GENERATION_ATTEMPTS
            val viewModel = createViewModel()
            var onSuccessCalled = false

            viewModel.addAsset(
                title = "Solder",
                stockStr = "1",
                statusStr = "Tersedia",
                category = "Perkakas",
                description = "",
                onSuccess = { onSuccessCalled = true }
            )

            assertTrue(onSuccessCalled.not())
            assertTrue(viewModel.addState.value is UiState.Error)
            assertEquals(0, assetRepository.addAssetCalls.size)
        }

    @Test
    fun `invalid stock input defaults to zero instead of crashing`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()

            viewModel.addAsset(
                title = "Asset Aneh",
                stockStr = "bukan-angka",
                statusStr = "Tersedia",
                category = "",
                description = "",
                onSuccess = {}
            )

            assertEquals(0, assetRepository.addAssetCalls.first().stock)
            assertEquals("All", assetRepository.addAssetCalls.first().category)
        }
}
