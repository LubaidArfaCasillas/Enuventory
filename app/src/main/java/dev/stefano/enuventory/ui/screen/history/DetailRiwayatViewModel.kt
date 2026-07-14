package dev.stefano.enuventory.ui.screen.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import dev.stefano.enuventory.domain.usecase.GetBorrowRecordByIdUseCase
import dev.stefano.enuventory.ui.common.UiState
import dev.stefano.enuventory.ui.pages.DetailRiwayatState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailRiwayatUiModel(
    val record: BorrowRecord,
    val riwayatState: DetailRiwayatState
)

@HiltViewModel
class DetailRiwayatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBorrowRecordByIdUseCase: GetBorrowRecordByIdUseCase
) : ViewModel() {

    val recordId: String = savedStateHandle.get<String>("recordId") ?: ""

    private val _uiState = MutableStateFlow<UiState<DetailRiwayatUiModel>>(UiState.Loading)
    val uiState: StateFlow<UiState<DetailRiwayatUiModel>> = _uiState.asStateFlow()

    init {
        loadRecord()
    }

    fun loadRecord() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val record = getBorrowRecordByIdUseCase(recordId)
                if (record != null) {
                    val riwayatState = when (record.status) {
                        BorrowStatus.Pending -> DetailRiwayatState.MenungguPersetujuan
                        BorrowStatus.Borrowed -> DetailRiwayatState.BatasKembali
                        BorrowStatus.Rejected -> DetailRiwayatState.Ditolak
                        BorrowStatus.Completed -> DetailRiwayatState.Dikembalikan
                    }
                    _uiState.value = UiState.Success(DetailRiwayatUiModel(record, riwayatState))
                } else {
                    _uiState.value = UiState.Error("Riwayat tidak ditemukan")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal memuat detail riwayat")
            }
        }
    }
}
