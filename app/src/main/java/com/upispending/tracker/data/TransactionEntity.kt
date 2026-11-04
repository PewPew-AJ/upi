package com.upispending.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upi_transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val merchant: String,
    val upiApp: String, // Google Pay, PhonePe, Paytm, CRED, BHIM, Bank SMS
    val category: String, // Food, Grocery, Shopping, Travel, Bills, Transfer, Other
    val referenceId: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val rawText: String,
    val isDebit: Boolean = true
)
