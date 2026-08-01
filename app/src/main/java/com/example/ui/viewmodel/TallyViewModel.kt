package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class CustomerFilter {
    ALL, RECEIVABLE, PAYABLE, FAVORITES
}

class TallyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = TallyRepository(database.businessDao(), database.customerDao(), database.transactionDao())
    val prefsRepository = PreferencesRepository(application)

    val language: StateFlow<String> = prefsRepository.language
    val selectedBusinessId: StateFlow<Long> = prefsRepository.selectedBusinessId
    val isDarkMode: StateFlow<Boolean> = prefsRepository.isDarkMode
    val pinEnabled: StateFlow<Boolean> = prefsRepository.pinEnabled
    val pinCode: StateFlow<String> = prefsRepository.pinCode
    val isAppLocked: StateFlow<Boolean> = prefsRepository.isAppLocked

    val businesses: StateFlow<List<BusinessEntity>> = repository.allBusinesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentBusinessSummary: StateFlow<BusinessSummary?> = selectedBusinessId
        .flatMapLatest { bizId -> repository.getBusinessSummary(bizId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val customersWithBalances: StateFlow<List<CustomerWithBalance>> = selectedBusinessId
        .flatMapLatest { bizId -> repository.getCustomersWithBalances(bizId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val allTransactions: StateFlow<List<TransactionEntity>> = selectedBusinessId
        .flatMapLatest { bizId -> repository.getTransactionsByBusiness(bizId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Customer Filter & Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _customerFilter = MutableStateFlow(CustomerFilter.ALL)
    val customerFilter: StateFlow<CustomerFilter> = _customerFilter.asStateFlow()

    val filteredCustomers: StateFlow<List<CustomerWithBalance>> = combine(
        customersWithBalances, searchQuery, customerFilter
    ) { list, query, filter ->
        list.filter { item ->
            val matchesQuery = item.customer.name.contains(query, ignoreCase = true) ||
                    item.customer.phone.contains(query) ||
                    item.customer.address.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                CustomerFilter.ALL -> true
                CustomerFilter.RECEIVABLE -> item.netBalance > 0
                CustomerFilter.PAYABLE -> item.netBalance < 0
                CustomerFilter.FAVORITES -> item.customer.isFavorite
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Customer for Details
    private val _selectedCustomerId = MutableStateFlow<Long?>(null)
    val selectedCustomerId: StateFlow<Long?> = _selectedCustomerId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedCustomer: StateFlow<CustomerEntity?> = _selectedCustomerId
        .flatMapLatest { id ->
            if (id != null) repository.getCustomerByIdFlow(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedCustomerTransactions: StateFlow<List<TransactionEntity>> = _selectedCustomerId
        .flatMapLatest { id ->
            if (id != null) repository.getTransactionsByCustomer(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.clearAllData()
            repository.ensureDefaultBusinessExists()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _selectedCustomerId.value = null
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCustomerFilter(filter: CustomerFilter) {
        _customerFilter.value = filter
    }

    fun selectCustomer(customerId: Long?) {
        _selectedCustomerId.value = customerId
    }

    fun switchBusiness(businessId: Long) {
        prefsRepository.setSelectedBusinessId(businessId)
    }

    fun setLanguage(lang: String) {
        prefsRepository.setLanguage(lang)
    }

    fun toggleDarkMode() {
        prefsRepository.setDarkMode(!isDarkMode.value)
    }

    fun setPinEnabled(enabled: Boolean) {
        prefsRepository.setPinEnabled(enabled)
    }

    fun setPinCode(code: String) {
        prefsRepository.setPinCode(code)
    }

    fun unlockApp(inputPin: String): Boolean {
        if (inputPin == pinCode.value) {
            prefsRepository.unlockApp()
            return true
        }
        return false
    }

    fun lockApp() {
        prefsRepository.lockApp()
    }

    fun toggleFavorite(customerId: Long, current: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(customerId, current)
        }
    }

    fun addCustomer(name: String, phone: String, address: String, notes: String) {
        viewModelScope.launch {
            val bizId = selectedBusinessId.value
            val newCustomer = CustomerEntity(
                businessId = bizId,
                name = name.trim(),
                phone = phone.trim(),
                address = address.trim(),
                notes = notes.trim()
            )
            repository.insertCustomer(newCustomer)
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            if (_selectedCustomerId.value == customer.id) {
                _selectedCustomerId.value = null
            }
        }
    }

    fun addTransaction(
        customerId: Long,
        type: TransactionType,
        amount: Double,
        method: PaymentMethod,
        note: String
    ) {
        viewModelScope.launch {
            val bizId = selectedBusinessId.value
            val txn = TransactionEntity(
                customerId = customerId,
                businessId = bizId,
                type = type,
                amount = amount,
                paymentMethod = method,
                note = note.trim(),
                timestamp = System.currentTimeMillis()
            )
            repository.insertTransaction(txn)
        }
    }

    fun deleteTransaction(txn: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(txn)
        }
    }

    fun addBusiness(name: String, ownerName: String, phone: String, address: String) {
        viewModelScope.launch {
            val biz = BusinessEntity(
                name = name.trim(),
                ownerName = ownerName.trim(),
                phone = phone.trim(),
                address = address.trim()
            )
            val newId = repository.insertBusiness(biz)
            prefsRepository.setSelectedBusinessId(newId)
        }
    }

    fun updateBusiness(business: BusinessEntity) {
        viewModelScope.launch {
            repository.updateBusiness(business)
        }
    }
}
