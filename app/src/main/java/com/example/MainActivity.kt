package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.data.local.entities.TransactionType
import com.example.ui.components.*
import com.example.ui.language.Strings
import com.example.ui.screens.*
import com.example.ui.theme.TallyKhataTheme
import com.example.ui.viewmodel.TallyViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TallyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val language by viewModel.language.collectAsState()
            val isLocked by viewModel.isAppLocked.collectAsState()

            TallyKhataTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLocked) {
                        PinLockOverlay(
                            language = language,
                            onUnlockAttempt = { pin -> viewModel.unlockApp(pin) }
                        )
                    } else {
                        MainAppContent(viewModel = viewModel, language = language)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    viewModel: TallyViewModel,
    language: String
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    val customers by viewModel.customersWithBalances.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val selectedCustomerTxns by viewModel.selectedCustomerTransactions.collectAsState()
    val businesses by viewModel.businesses.collectAsState()
    val selectedBizId by viewModel.selectedBusinessId.collectAsState()

    val currentBiz = businesses.find { it.id == selectedBizId }

    // Dialog state handlers
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var showAddTxnDialog by remember { mutableStateOf(false) }
    var initialTxnType by remember { mutableStateOf(TransactionType.RECEIVABLE) }
    var showCalculatorDialog by remember { mutableStateOf(false) }
    var showQrCodeDialog by remember { mutableStateOf(false) }
    var showInvoiceShareDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (currentRoute != "customer_detail") {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        onClick = {
                            navController.navigate("dashboard") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        label = { Text(Strings.get("nav_home", language), fontWeight = FontWeight.SemiBold) }
                    )

                    NavigationBarItem(
                        selected = currentRoute == "customers",
                        onClick = {
                            navController.navigate("customers") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.People, contentDescription = null) },
                        label = { Text(Strings.get("nav_customers", language), fontWeight = FontWeight.SemiBold) }
                    )

                    NavigationBarItem(
                        selected = currentRoute == "reports",
                        onClick = {
                            navController.navigate("reports") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                        label = { Text(Strings.get("nav_reports", language), fontWeight = FontWeight.SemiBold) }
                    )

                    NavigationBarItem(
                        selected = currentRoute == "settings",
                        onClick = {
                            navController.navigate("settings") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(Strings.get("settings", language), fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    viewModel = viewModel,
                    language = language,
                    onNavigateToCustomers = { navController.navigate("customers") },
                    onNavigateToReports = { navController.navigate("reports") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onOpenAddCustomer = { showAddCustomerDialog = true },
                    onOpenAddTransaction = {
                        initialTxnType = TransactionType.RECEIVABLE
                        showAddTxnDialog = true
                    },
                    onOpenCalculator = { showCalculatorDialog = true },
                    onOpenQrCode = { showQrCodeDialog = true },
                    onCustomerClick = { customerId ->
                        viewModel.selectCustomer(customerId)
                        navController.navigate("customer_detail")
                    }
                )
            }

            composable("customers") {
                CustomersScreen(
                    viewModel = viewModel,
                    language = language,
                    onCustomerClick = { customerId ->
                        viewModel.selectCustomer(customerId)
                        navController.navigate("customer_detail")
                    },
                    onOpenAddCustomer = { showAddCustomerDialog = true }
                )
            }

            composable("customer_detail") {
                CustomerDetailScreen(
                    viewModel = viewModel,
                    language = language,
                    onBack = { navController.popBackStack() },
                    onOpenAddTransactionWithType = { type ->
                        initialTxnType = type
                        showAddTxnDialog = true
                    },
                    onOpenInvoiceShare = { showInvoiceShareDialog = true }
                )
            }

            composable("reports") {
                ReportsScreen(
                    viewModel = viewModel,
                    language = language,
                    onOpenInvoiceShare = { showInvoiceShareDialog = true }
                )
            }

            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    language = language
                )
            }
        }
    }

    // Global Dialog overlays
    if (showAddCustomerDialog) {
        AddCustomerDialog(
            language = language,
            onDismiss = { showAddCustomerDialog = false },
            onSave = { name, phone, address, notes ->
                viewModel.addCustomer(name, phone, address, notes)
            }
        )
    }

    if (showAddTxnDialog) {
        AddTransactionDialog(
            language = language,
            customers = customers,
            initialCustomerId = selectedCustomer?.id,
            initialType = initialTxnType,
            onDismiss = { showAddTxnDialog = false },
            onOpenCalculator = { showCalculatorDialog = true },
            onSave = { custId, type, amt, method, note ->
                viewModel.addTransaction(custId, type, amt, method, note)
            }
        )
    }

    if (showCalculatorDialog) {
        QuickCalculatorDialog(
            language = language,
            onDismiss = { showCalculatorDialog = false },
            onApplyAmount = { amt ->
                showAddTxnDialog = true
            }
        )
    }

    if (showQrCodeDialog) {
        QrCodeDialog(
            language = language,
            businessName = currentBiz?.name ?: "Tally Khata Store",
            ownerPhone = currentBiz?.phone ?: "01700000000",
            onDismiss = { showQrCodeDialog = false }
        )
    }

    if (showInvoiceShareDialog && selectedCustomer != null) {
        InvoiceShareDialog(
            language = language,
            businessName = currentBiz?.name ?: "Tally Khata Store",
            customer = selectedCustomer!!,
            transactions = selectedCustomerTxns,
            onDismiss = { showInvoiceShareDialog = false }
        )
    }
}
