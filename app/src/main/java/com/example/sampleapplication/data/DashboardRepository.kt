package com.example.sampleapplication.data

import com.example.sampleapplication.model.Transaction
import com.example.sampleapplication.model.UserBalance
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

interface DashboardRepository {
    suspend fun fetchUserBalance(): UserBalance
    suspend fun fetchRecentTransactions(): List<Transaction>
}

@Singleton
class DashboardRepositoryImpl @Inject constructor() : DashboardRepository {

    override suspend fun fetchUserBalance(): UserBalance {
        return try {
            // Simulate network delay
            delay(2000)
            UserBalance(balance = 12500.50, currency = "USD")
        } catch (e: CancellationException) {
            // Rethrow CancellationException to preserve structured concurrency
            throw e
        } catch (e: Exception) {
            // For a sample app, we'll just throw the exception
            // In a real app, you might map this to a custom domain error
            throw e
        }
    }

    override suspend fun fetchRecentTransactions(): List<Transaction> {
        return try {
            // Simulate network delay
            delay(2000)
            listOf(
                Transaction(UUID.randomUUID().toString(), "Amazon", -120.50),
                Transaction(UUID.randomUUID().toString(), "Apple Store", -999.00),
                Transaction(UUID.randomUUID().toString(), "Salary", 5000.00),
                Transaction(UUID.randomUUID().toString(), "Starbucks", -5.75)
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }
}
