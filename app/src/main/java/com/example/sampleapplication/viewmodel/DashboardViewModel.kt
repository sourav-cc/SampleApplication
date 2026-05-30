package com.example.sampleapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampleapplication.data.DashboardRepository
import com.example.sampleapplication.ui.event.DashboardUiEvent
import com.example.sampleapplication.ui.state.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<DashboardUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                // Using coroutineScope to await multiple async blocks
                coroutineScope {
                    val balanceDeferred = async { repository.fetchUserBalance() }
                    val transactionsDeferred = async { repository.fetchRecentTransactions() }

                    val balance = balanceDeferred.await()
                    val transactions = transactionsDeferred.await()

                    _uiState.value = DashboardUiState.Success(balance, transactions)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun onTransactionClicked(merchant: String) {
        viewModelScope.launch {
            _uiEvent.send(DashboardUiEvent.ShowToast("Clicked on $merchant"))
        }
    }

    fun onRefreshClicked() {
        loadDashboardData()
    }
}
