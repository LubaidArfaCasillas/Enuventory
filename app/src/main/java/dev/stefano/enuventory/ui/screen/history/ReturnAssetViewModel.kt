package dev.stefano.enuventory.ui.screen.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.usecase.GetBorrowRecordByIdUseCase
import dev.stefano.enuventory.domain.usecase.ReturnAssetUseCase
import dev.stefano.enuventory.ui.common.UiState
import dev.stefano.enuventory.ui.pages.UploadBuktiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReturnAssetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBorrowRecordByIdUseCase: GetBorrowRecordByIdUseCase,
    private val returnAssetUseCase: ReturnAssetUseCase
) : ViewModel() {

    val recordId: String = savedStateHandle.get<String>("recordId") ?: ""

    private val _recordState = MutableStateFlow<UiState<BorrowRecord>>(UiState.Loading)
    val recordState: StateFlow<UiState<BorrowRecord>> = _recordState.asStateFlow()

    private val _uploadState = MutableStateFlow(UploadBuktiState.Capture)
    val uploadState: StateFlow<UploadBuktiState> = _uploadState.asStateFlow()

    init {
        loadRecord()
    }

    fun loadRecord() {
        viewModelScope.launch {
            _recordState.value = UiState.Loading
            try {
                val record = getBorrowRecordByIdUseCase(recordId)
                if (record != null) {
                    _recordState.value = UiState.Success(record)
                } else {
                    _recordState.value = UiState.Error("Riwayat tidak ditemukan")
                }
            } catch (e: Exception) {
                _recordState.value = UiState.Error(e.message ?: "Gagal memuat detail riwayat")
            }
        }
    }

    fun onCapturePhoto() {
        _uploadState.value = UploadBuktiState.PreviewImage
    }

    fun onUlangiCapture() {
        _uploadState.value = UploadBuktiState.Capture
    }

    fun submitReturn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uploadState.value = UploadBuktiState.Submitting
            try {
                // Return via ReturnAssetUseCase using a dummy proof image for MVP
                val dummyProofUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=500"
                returnAssetUseCase(recordId, dummyProofUrl)
                onSuccess()
            } catch (e: Exception) {
                _uploadState.value = UploadBuktiState.Error
            }
        }
    }
}
