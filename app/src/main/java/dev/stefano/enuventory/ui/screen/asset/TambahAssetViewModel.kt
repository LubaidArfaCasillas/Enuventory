package dev.stefano.enuventory.ui.screen.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stefano.enuventory.domain.model.Asset
import dev.stefano.enuventory.domain.model.AssetStatus
import dev.stefano.enuventory.domain.usecase.AddAssetUseCase
import dev.stefano.enuventory.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TambahAssetViewModel @Inject constructor(
    private val addAssetUseCase: AddAssetUseCase
) : ViewModel() {

    private val _addState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val addState: StateFlow<UiState<Unit>> = _addState.asStateFlow()

    fun addAsset(
        title: String,
        stockStr: String,
        statusStr: String,
        category: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        val stock = stockStr.toIntOrNull() ?: 0
        val status = when (statusStr) {
            "Tersedia" -> AssetStatus.Available
            "Tidak Tersedia" -> AssetStatus.Unavailable
            else -> AssetStatus.Maintenance
        }

        viewModelScope.launch {
            _addState.value = UiState.Loading
            try {
                // Generate simple unique ID
                val assetId = "HW-${(System.currentTimeMillis() % 100000).toString().padStart(5, '0')}"
                val asset = Asset(
                    id = assetId,
                    title = title,
                    stock = stock,
                    status = status,
                    category = category.ifBlank { "All" },
                    description = description
                )
                addAssetUseCase(asset)
                _addState.value = UiState.Success(Unit)
                onSuccess()
            } catch (e: Exception) {
                _addState.value = UiState.Error(e.message ?: "Gagal menambahkan asset")
            }
        }
    }

    fun resetState() {
        _addState.value = UiState.Success(Unit)
    }
}
