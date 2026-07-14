package dev.stefano.enuventory.ui.screen.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stefano.enuventory.domain.usecase.GetCurrentUserUseCase
import dev.stefano.enuventory.domain.usecase.GetUserBorrowHistoryUseCase
import dev.stefano.enuventory.ui.common.UiState
import dev.stefano.enuventory.ui.util.deadlineMessage
import dev.stefano.enuventory.ui.util.nearDeadlineRecords
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserNotificationViewModel @Inject constructor(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getUserBorrowHistoryUseCase: GetUserBorrowHistoryUseCase
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val notificationsState: StateFlow<UiState<List<NotificationItem>>> = getCurrentUserUseCase()
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else getUserBorrowHistoryUseCase(user.uid)
        }
        .map { records ->
            val nearDeadline = records.nearDeadlineRecords()
            if (nearDeadline.isEmpty()) UiState.Empty
            else UiState.Success(
                nearDeadline.map { record ->
                    NotificationItem(
                        id = record.id,
                        title = "Batas Pengembalian Segera",
                        message = "\"${record.assetTitle}\" -- ${record.deadlineMessage()}"
                    )
                }
            )
        }
        .catch { e -> emit(UiState.Error(e.message ?: "Gagal memuat notifikasi")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )
}
