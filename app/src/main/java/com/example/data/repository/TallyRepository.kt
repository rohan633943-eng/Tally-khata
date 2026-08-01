package com.example.data.repository

import com.example.data.local.dao.BusinessDao
import com.example.data.local.dao.CustomerDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class CustomerWithBalance(
    val customer: CustomerEntity,
    val totalReceivable: Double, // পাবো
    val totalPayable: Double,    // দেবো
    val netBalance: Double,      // Positive = Customer owes you (পাবো), Negative = You owe customer (দেবো)
    val lastTransactionTime: Long = 0L
)

data class BusinessSummary(
    val totalCustomers: Int,
    val totalReceivable: Double, // মোট পাবো (Green)
    val totalPayable: Double,    // মোট দেবো (Red)
    val netCashflow: Double,
    val todayReceivable: Double,
    val todayPayable: Double
)

class TallyRepository(
    private val businessDao: BusinessDao,
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao
) {
    val allBusinesses: Flow<List<BusinessEntity>> = businessDao.getAllBusinesses()

    fun getCustomersWithBalances(businessId: Long): Flow<List<CustomerWithBalance>> {
        return combine(
            customerDao.getCustomersByBusiness(businessId),
            transactionDao.getTransactionsByBusiness(businessId)
        ) { customers, transactions ->
            customers.map { customer ->
                val customerTxns = transactions.filter { it.customerId == customer.id }
                val receivable = customerTxns.filter { it.type == TransactionType.RECEIVABLE }.sumOf { it.amount }
                val payable = customerTxns.filter { it.type == TransactionType.PAYABLE }.sumOf { it.amount }
                val net = receivable - payable
                val lastTime = customerTxns.maxOfOrNull { it.timestamp } ?: customer.createdAt
                CustomerWithBalance(
                    customer = customer,
                    totalReceivable = receivable,
                    totalPayable = payable,
                    netBalance = net,
                    lastTransactionTime = lastTime
                )
            }
        }
    }

    fun getBusinessSummary(businessId: Long): Flow<BusinessSummary> {
        return combine(
            customerDao.getCustomersByBusiness(businessId),
            transactionDao.getTransactionsByBusiness(businessId)
        ) { customers, transactions ->
            val totalReceivables = transactions.filter { it.type == TransactionType.RECEIVABLE }.sumOf { it.amount }
            val totalPayables = transactions.filter { it.type == TransactionType.PAYABLE }.sumOf { it.amount }

            val now = System.currentTimeMillis()
            val startOfDay = now - (now % (24 * 60 * 60 * 1000))
            val todayTxns = transactions.filter { it.timestamp >= startOfDay }
            val todayRec = todayTxns.filter { it.type == TransactionType.RECEIVABLE }.sumOf { it.amount }
            val todayPay = todayTxns.filter { it.type == TransactionType.PAYABLE }.sumOf { it.amount }

            BusinessSummary(
                totalCustomers = customers.size,
                totalReceivable = totalReceivables,
                totalPayable = totalPayables,
                netCashflow = totalReceivables - totalPayables,
                todayReceivable = todayRec,
                todayPayable = todayPay
            )
        }
    }

    fun getTransactionsByBusiness(businessId: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByBusiness(businessId)
    }

    fun getTransactionsByCustomer(customerId: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCustomer(customerId)
    }

    fun getCustomerByIdFlow(customerId: Long): Flow<CustomerEntity?> {
        return customerDao.getCustomerByIdFlow(customerId)
    }

    suspend fun getCustomerById(customerId: Long): CustomerEntity? {
        return customerDao.getCustomerById(customerId)
    }

    suspend fun insertBusiness(business: BusinessEntity): Long {
        return businessDao.insertBusiness(business)
    }

    suspend fun updateBusiness(business: BusinessEntity) {
        businessDao.updateBusiness(business)
    }

    suspend fun insertCustomer(customer: CustomerEntity): Long {
        return customerDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: CustomerEntity) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: CustomerEntity) {
        transactionDao.deleteTransactionsByCustomer(customer.id)
        customerDao.deleteCustomer(customer)
    }

    suspend fun toggleFavorite(customerId: Long, currentStatus: Boolean) {
        customerDao.updateFavoriteStatus(customerId, !currentStatus)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun clearAllData() {
        transactionDao.deleteAllTransactions()
        customerDao.deleteAllCustomers()
    }

    suspend fun ensureDefaultBusinessExists() {
        val businesses = businessDao.getBusinessById(1)
        if (businesses == null) {
            businessDao.insertBusiness(
                BusinessEntity(
                    id = 1,
                    name = "মেসার্স রোহান ট্রেডার্স",
                    ownerName = "রোহান আহমেদ",
                    phone = "01700000000",
                    address = "চকবাজার, ঢাকা - ১২১১",
                    isDefault = true
                )
            )
        }
    }

    suspend fun seedSampleDataIfEmpty() {
        val businesses = businessDao.getBusinessById(1)
        if (businesses == null) {
            val defaultBizId = businessDao.insertBusiness(
                BusinessEntity(
                    id = 1,
                    name = "মেসার্স রোহান ট্রেডার্স",
                    ownerName = "রোহান আহমেদ",
                    phone = "01700000000",
                    address = "চকবাজার, ঢাকা - ১২১১",
                    isDefault = true
                )
            )

            // Seed customers
            val c1 = customerDao.insertCustomer(
                CustomerEntity(
                    businessId = defaultBizId,
                    name = "রহিম ভাই (মুদি দোকান)",
                    phone = "01711223344",
                    address = "দোকান নং ৪, চকবাজার",
                    notes = "প্রতি সপ্তাহের শনিবারে বিল দেন",
                    isFavorite = true,
                    avatarColorHex = "#006A36"
                )
            )

            val c2 = customerDao.insertCustomer(
                CustomerEntity(
                    businessId = defaultBizId,
                    name = "করিম ডিপার্টমেন্টাল স্টোর",
                    phone = "01822334455",
                    address = "নিউ মার্কেট, ঢাকা",
                    notes = "পাইকারী কাস্টমার",
                    isFavorite = true,
                    avatarColorHex = "#1976D2"
                )
            )

            val c3 = customerDao.insertCustomer(
                CustomerEntity(
                    businessId = defaultBizId,
                    name = "সুমন ইলেকট্রনিক্স",
                    phone = "01933445566",
                    address = "স্টেডিয়াম মার্কেট",
                    notes = "সাপ্লায়ার",
                    isFavorite = false,
                    avatarColorHex = "#D32F2F"
                )
            )

            val now = System.currentTimeMillis()
            val dayMs = 86400000L

            // Seed Transactions
            transactionDao.insertTransaction(
                TransactionEntity(
                    customerId = c1,
                    businessId = defaultBizId,
                    type = TransactionType.RECEIVABLE,
                    amount = 4500.0,
                    paymentMethod = PaymentMethod.GOODS,
                    note = "৫০ কেজী চাল ও চালের বস্তা বাবদ বাকী",
                    timestamp = now - (dayMs * 2)
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    customerId = c1,
                    businessId = defaultBizId,
                    type = TransactionType.PAYABLE,
                    amount = 1500.0,
                    paymentMethod = PaymentMethod.BKASH,
                    note = "বিকাশে জমা করেছেন",
                    timestamp = now - (dayMs * 1)
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    customerId = c2,
                    businessId = defaultBizId,
                    type = TransactionType.RECEIVABLE,
                    amount = 12800.0,
                    paymentMethod = PaymentMethod.GOODS,
                    note = "তেল ও সয়াবিন বোতল পাইকারী মাল",
                    timestamp = now - (dayMs * 3)
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    customerId = c2,
                    businessId = defaultBizId,
                    type = TransactionType.PAYABLE,
                    amount = 5000.0,
                    paymentMethod = PaymentMethod.NAGAD,
                    note = "নগদ অ্যাপে আংশিক জমা",
                    timestamp = now - (1000 * 60 * 120)
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    customerId = c3,
                    businessId = defaultBizId,
                    type = TransactionType.PAYABLE,
                    amount = 8500.0,
                    paymentMethod = PaymentMethod.BANK,
                    note = "সাপ্লায়ার সুমন ভাইকে ক্যাশ পেমেন্ট দেওয়া হয়েছে",
                    timestamp = now - (dayMs * 4)
                )
            )
        }
    }
}
