package dev.stefano.enuventory.ui.screen.approval

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.usecase.ApproveRequestUseCase
import dev.stefano.enuventory.domain.usecase.GetBorrowRecordByIdUseCase
import dev.stefano.enuventory.domain.usecase.RejectRequestUseCase
import dev.stefano.enuventory.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailRequestViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBorrowRecordByIdUseCase: GetBorrowRecordByIdUseCase,
    private val approveRequestUseCase: ApproveRequestUseCase,
    private val rejectRequestUseCase: RejectRequestUseCase
) : ViewModel() {

    val recordId: String = savedStateHandle.get<String>("recordId") ?: ""

    private val _uiState = MutableStateFlow<UiState<BorrowRecord>>(UiState.Loading)
    val uiState: StateFlow<UiState<BorrowRecord>> = _uiState.asStateFlow()

    init {
        loadRecord()
    }

    fun loadRecord() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val record = getBorrowRecordByIdUseCase(recordId)
                if (record != null) {
                    _uiState.value = UiState.Success(record)
                } else {
                    _uiState.value = UiState.Error("Request tidak ditemukan")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal memuat detail request")
            }
        }
    }

    fun approveRequest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                approveRequestUseCase(recordId)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal menyetujui request")
            }
        }
    }

    fun rejectRequest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                rejectRequestUseCase(recordId)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal menolak request")
            }
        }
    }
}
