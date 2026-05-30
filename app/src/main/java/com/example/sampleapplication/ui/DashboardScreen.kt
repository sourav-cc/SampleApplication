package com.example.sampleapplication.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sampleapplication.model.Transaction
import com.example.sampleapplication.model.UserBalance
import com.example.sampleapplication.ui.event.DashboardUiEvent
import com.example.sampleapplication.ui.state.DashboardUiState
import com.example.sampleapplication.ui.theme.SampleApplicationTheme
import com.example.sampleapplication.viewmodel.DashboardViewModel

import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is DashboardUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                DashboardUiEvent.MapsToDetails -> {
                    // Navigate to details (not implemented in this sample)
                }
            }
        }
    }

    DashboardScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.onRefreshClicked() },
        onTransactionClick = { viewModel.onTransactionClicked(it.merchant) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenContent(
    uiState: DashboardUiState,
    onRefresh: () -> Unit,
    onTransactionClick: (Transaction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance Dashboard") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is DashboardUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DashboardUiState.Success -> {
                    DashboardContent(
                        balance = uiState.balance,
                        transactions = uiState.transactions,
                        onTransactionClick = onTransactionClick
                    )
                }
                is DashboardUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardContent(
    balance: UserBalance,
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BalanceCard(balance)
        }
        item {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(transactions) { transaction ->
            TransactionItem(transaction, onClick = { onTransactionClick(transaction) })
        }
    }
}

@Composable
fun BalanceCard(userBalance: UserBalance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${userBalance.currency} ${String.format(Locale.US, "%.2f", userBalance.balance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.merchant,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "ID: ${transaction.id.take(8)}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = (if (transaction.amount > 0) "+" else "") + String.format(Locale.US, "%.2f", transaction.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (transaction.amount > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    SampleApplicationTheme {
        DashboardScreenContent(
            uiState = DashboardUiState.Success(
                balance = UserBalance(12500.50, "USD"),
                transactions = listOf(
                    Transaction("1", "Amazon", -120.50),
                    Transaction("2", "Apple Store", -999.00),
                    Transaction("3", "Salary", 5000.00),
                    Transaction("4", "Starbucks", -5.75)
                )
            ),
            onRefresh = {},
            onTransactionClick = {}
        )
    }
}
