package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    RECEIVABLE, // পাবো (Credit - Customer owes shopkeeper)
    PAYABLE     // দেবো (Debit - Shopkeeper gave/paid or received money)
}

enum class PaymentMethod {
    CASH,    // নগদ
    BKASH,   // বিকাশ
    NAGAD,   // নগদ অ্যাপ
    ROCKET,  // রকেট
    BANK,    // ব্যাংক
    GOODS    // বাকীতে পণ্য
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val businessId: Long = 1,
    val type: TransactionType,
    val amount: Double,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val note: String = "",
    val billImageUri: String? = null,
    val voiceNotePath: String? = null,
    val isPartialPayment: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
