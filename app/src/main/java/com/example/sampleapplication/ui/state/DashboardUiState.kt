package com.example.sampleapplication.ui.state

import com.example.sampleapplication.model.Transaction
import com.example.sampleapplication.model.UserBalance

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    
    data class Success(
        val balance: UserBalance,
        val transactions: List<Transaction>
    ) : DashboardUiState
    
    data class Error(val message: String) : DashboardUiState
}
