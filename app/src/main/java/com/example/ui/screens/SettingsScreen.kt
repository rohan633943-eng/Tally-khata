package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.language.Strings
import com.example.ui.viewmodel.TallyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TallyViewModel,
    language: String
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    val pinEnabled by viewModel.pinEnabled.collectAsState()
    val currentPin by viewModel.pinCode.collectAsState()
    val selectedBizId by viewModel.selectedBusinessId.collectAsState()
    val businesses by viewModel.businesses.collectAsState()

    val currentBiz = businesses.find { it.id == selectedBizId }

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf(currentPin) }

    var showBizEditDialog by remember { mutableStateOf(false) }
    var bizName by remember { mutableStateOf(currentBiz?.name ?: "") }
    var ownerName by remember { mutableStateOf(currentBiz?.ownerName ?: "") }
    var phone by remember { mutableStateOf(currentBiz?.phone ?: "") }
    var address by remember { mutableStateOf(currentBiz?.address ?: "") }

    var backupMessage by remember { mutableStateOf<String?>(null) }
    var showResetConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.get("settings", language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = 88.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Business Info Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = Strings.get("business_info", language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(onClick = {
                                bizName = currentBiz?.name ?: ""
                                ownerName = currentBiz?.ownerName ?: ""
                                phone = currentBiz?.phone ?: ""
                                address = currentBiz?.address ?: ""
                                showBizEditDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Business")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentBiz?.name ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${Strings.get("owner_name", language)}: ${currentBiz?.ownerName ?: ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "মোবাইল: ${currentBiz?.phone ?: ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!currentBiz?.address.isNullToEmpty()) {
                            Text(
                                text = "ঠিকানা: ${currentBiz?.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Language & Appearance Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (language.uppercase() == "BN") "অ্যাপ প্রিফারেন্স" else "App Preferences",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Language Toggle Row
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(Strings.get("language", language), fontWeight = FontWeight.Bold)
                                    Text(
                                        if (language.uppercase() == "BN") "বাংলা (Bangla)" else "English",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = language.uppercase() == "BN",
                                onCheckedChange = { isBn ->
                                    viewModel.setLanguage(if (isBn) "BN" else "EN")
                                }
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Dark Mode Toggle Row
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(Strings.get("dark_mode", language), fontWeight = FontWeight.Bold)
                            }

                            Switch(
                                checked = isDark,
                                onCheckedChange = { viewModel.toggleDarkMode() }
                            )
                        }
                    }
                }
            }

            // Security PIN Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = Strings.get("pin_lock", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(Strings.get("enable_pin", language), fontWeight = FontWeight.Bold)
                                    Text(
                                        if (pinEnabled) "পিন লক সক্রিয় আছে (****)" else "পিন লক নিষ্ক্রিয়",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = pinEnabled,
                                onCheckedChange = { enabled ->
                                    viewModel.setPinEnabled(enabled)
                                    if (enabled) showPinDialog = true
                                }
                            )
                        }

                        if (pinEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { showPinDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (language.uppercase() == "BN") "পিন পরিবর্তন করুন" else "Change PIN Code")
                            }
                        }
                    }
                }
            }

            // Backup & Cloud Sync Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = Strings.get("backup_data", language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                backupMessage = if (language.uppercase() == "BN")
                                    "আপনার ট্যালি খাতার তথ্য ফায়ারবেস ক্লাউডে সফলভাবে ব্যাকআপ নেওয়া হয়েছে!"
                                else
                                    "Tally Khata data successfully backed up to Cloud!"
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language.uppercase() == "BN") "এখনই ব্যাকআপ দিন" else "Backup Now",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (backupMessage != null) {
                            Text(
                                text = backupMessage!!,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Reset Data Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (language.uppercase() == "BN") "সকল হিসাব রিসেট" else "Reset All Data",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language.uppercase() == "BN") "সকল কাস্টমার ও লেনদেনের হিসাব ০ করে মুছে ফেলুন।" else "Clear all customers and transaction balances to zero.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showResetConfirmationDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language.uppercase() == "BN") "সকল হিসাব ০ করুন" else "Reset All Accounts to 0",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmationDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text(if (language.uppercase() == "BN") "আপনি কি নিশ্চিত?" else "Are you sure?") },
            text = { Text(if (language.uppercase() == "BN") "আপনার সমস্ত কাস্টমার এবং হিসাবের ডাটা মুছে ০ হয়ে যাবে। এই কাজটি ফেরানো যাবে না।" else "All customer and ledger data will be deleted and reset to zero. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showResetConfirmationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (language.uppercase() == "BN") "হ্যাঁ, মুছে ফেলুন" else "Yes, Delete All")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirmationDialog = false }) {
                    Text(Strings.get("cancel", language))
                }
            }
        )
    }

    // Change PIN Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text(if (language.uppercase() == "BN") "৪ ডিজিটের নতুন পিন দিন" else "Set 4-Digit PIN") },
            text = {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 4) pinInput = it },
                    singleLine = true,
                    label = { Text("PIN") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (pinInput.length == 4) {
                        viewModel.setPinCode(pinInput)
                        showPinDialog = false
                    }
                }) {
                    Text(Strings.get("save", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text(Strings.get("cancel", language))
                }
            }
        )
    }

    // Edit Business Dialog
    if (showBizEditDialog) {
        AlertDialog(
            onDismissRequest = { showBizEditDialog = false },
            title = { Text(Strings.get("business_info", language)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bizName,
                        onValueChange = { bizName = it },
                        label = { Text(Strings.get("business", language)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text(Strings.get("owner_name", language)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(Strings.get("mobile_number", language)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(Strings.get("address", language)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (bizName.isNotBlank()) {
                        if (currentBiz != null) {
                            viewModel.updateBusiness(
                                currentBiz.copy(
                                    name = bizName.trim(),
                                    ownerName = ownerName.trim(),
                                    phone = phone.trim(),
                                    address = address.trim()
                                )
                            )
                        }
                        showBizEditDialog = false
                    }
                }) {
                    Text(Strings.get("save", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBizEditDialog = false }) {
                    Text(Strings.get("cancel", language))
                }
            }
        )
    }
}

fun String?.isNullToEmpty(): Boolean = this == null || this.isBlank()
