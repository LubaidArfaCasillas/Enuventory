package dev.stefano.enuventory.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stefano.enuventory.domain.model.Asset
import dev.stefano.enuventory.domain.model.Category
import dev.stefano.enuventory.domain.usecase.GetAssetsUseCase
import dev.stefano.enuventory.domain.usecase.GetCategoriesUseCase
import dev.stefano.enuventory.ui.common.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val ALL_CATEGORIES_LABEL = "All"

@HiltViewModel
class HomeViewModel @Inject constructor(
    getAssetsUseCase: GetAssetsUseCase,
    getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    val assetsState: StateFlow<UiState<List<Asset>>> = getAssetsUseCase()
        .map { assets ->
            if (assets.isEmpty()) UiState.Empty
            else UiState.Success(assets)
        }
        .catch { e -> emit(UiState.Error(e.message ?: "Terjadi kesalahan")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    // Badge filter kategori di Home -- disinkronkan dengan kategori beneran yang
    // dikelola lewat layar Kelola Kategori, bukan daftar hardcoded.
    val categoriesState: StateFlow<List<String>> = getCategoriesUseCase()
        .map { categories -> listOf(ALL_CATEGORIES_LABEL) + categories.map(Category::name) }
        .catch { emit(listOf(ALL_CATEGORIES_LABEL)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf(ALL_CATEGORIES_LABEL)
        )
}
