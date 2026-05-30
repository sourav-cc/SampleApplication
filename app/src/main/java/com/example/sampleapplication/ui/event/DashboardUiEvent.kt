package com.example.sampleapplication.ui.event

sealed interface DashboardUiEvent {
    data class ShowToast(val message: String) : DashboardUiEvent
    data object MapsToDetails : DashboardUiEvent
}
