package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.TransactionType
import com.example.data.repository.CustomerWithBalance
import com.example.ui.language.Strings
import com.example.ui.theme.CreditGreen
import com.example.ui.theme.DebitRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    language: String,
    customers: List<CustomerWithBalance>,
    initialCustomerId: Long? = null,
    initialType: TransactionType = TransactionType.RECEIVABLE,
    onDismiss: () -> Unit,
    onOpenCalculator: () -> Unit,
    onSave: (customerId: Long, type: TransactionType, amount: Double, method: PaymentMethod, note: String) -> Unit
) {
    var selectedCustId by remember { mutableStateOf(initialCustomerId ?: customers.firstOrNull()?.customer?.id ?: 0L) }
    var txnType by remember { mutableStateOf(initialType) }
    var amountText by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var noteText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    var dropdownExpanded by remember { mutableStateOf(false) }

    val selectedCustName = customers.find { it.customer.id == selectedCustId }?.customer?.name
        ?: if (language.uppercase() == "BN") "কাস্টমার নির্বাচন করুন" else "Select Customer"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = Strings.get("add_entry", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Transaction Type Switcher (পাবো vs দেবো)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (txnType == TransactionType.RECEIVABLE) CreditGreen else Color.Transparent)
                            .clickable { txnType = TransactionType.RECEIVABLE }
                    ) {
                        Text(
                            text = Strings.get("pabo_badge", language) + " (+)",
                            fontWeight = FontWeight.Bold,
                            color = if (txnType == TransactionType.RECEIVABLE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (txnType == TransactionType.PAYABLE) DebitRed else Color.Transparent)
                            .clickable { txnType = TransactionType.PAYABLE }
                    ) {
                        Text(
                            text = Strings.get("debo_badge", language) + " (-)",
                            fontWeight = FontWeight.Bold,
                            color = if (txnType == TransactionType.PAYABLE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Customer Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCustName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(Strings.get("customer_name", language)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        customers.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(item.customer.name, fontWeight = FontWeight.Bold)
                                        Text(
                                            item.customer.phone,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCustId = item.customer.id
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Amount Field with Calculator trigger
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; showError = false },
                    label = { Text(Strings.get("enter_amount", language) + " (৳)") },
                    prefix = { Text("৳ ", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                    trailingIcon = {
                        IconButton(onClick = onOpenCalculator) {
                            Icon(Icons.Default.Calculate, contentDescription = "Calculator", tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Increment Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    listOf(100, 500, 1000, 5000).forEach { addAmt ->
                        SuggestionChip(
                            onClick = {
                                val current = amountText.toDoubleOrNull() ?: 0.0
                                amountText = (current + addAmt).toInt().toString()
                            },
                            label = { Text("+৳$addAmt", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method Selector
                Text(
                    text = Strings.get("payment_method", language),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    PaymentMethod.values().take(4).forEach { method ->
                        val isSel = selectedMethod == method
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedMethod = method },
                            label = {
                                Text(
                                    when (method) {
                                        PaymentMethod.CASH -> Strings.get("cash", language)
                                        PaymentMethod.BKASH -> Strings.get("bkash", language)
                                        PaymentMethod.NAGAD -> Strings.get("nagad", language)
                                        PaymentMethod.ROCKET -> Strings.get("rocket", language)
                                        else -> method.name
                                    },
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(Strings.get("notes_optional", language)) },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = if (language.uppercase() == "BN") "সঠিক টাকার পরিমাণ দিন" else "Please enter a valid amount",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(Strings.get("cancel", language))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull()
                            if (amt == null || amt <= 0.0 || selectedCustId <= 0) {
                                showError = true
                            } else {
                                onSave(selectedCustId, txnType, amt, selectedMethod, noteText)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (txnType == TransactionType.RECEIVABLE) CreditGreen else DebitRed
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(Strings.get("save", language), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
