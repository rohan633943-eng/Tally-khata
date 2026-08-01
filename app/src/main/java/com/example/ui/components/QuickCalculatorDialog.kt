package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.language.Strings

@Composable
fun QuickCalculatorDialog(
    language: String,
    onDismiss: () -> Unit,
    onApplyAmount: (Double) -> Unit
) {
    var display by remember { mutableStateOf("0") }
    var operand1 by remember { mutableDoubleStateOf(0.0) }
    var pendingOp by remember { mutableStateOf<String?>(null) }
    var newNumberStarting by remember { mutableStateOf(false) }

    fun onNum(n: String) {
        if (display == "0" || newNumberStarting) {
            display = n
            newNumberStarting = false
        } else {
            if (display.length < 10) display += n
        }
    }

    fun onOp(op: String) {
        val currentVal = display.toDoubleOrNull() ?: 0.0
        if (pendingOp != null && !newNumberStarting) {
            val res = when (pendingOp) {
                "+" -> operand1 + currentVal
                "-" -> operand1 - currentVal
                "×" -> operand1 * currentVal
                "÷" -> if (currentVal != 0.0) operand1 / currentVal else 0.0
                else -> currentVal
            }
            display = if (res % 1.0 == 0.0) res.toLong().toString() else "%.2f".format(res)
            operand1 = res
        } else {
            operand1 = currentVal
        }
        pendingOp = op
        newNumberStarting = true
    }

    fun onEquals() {
        val currentVal = display.toDoubleOrNull() ?: 0.0
        if (pendingOp != null) {
            val res = when (pendingOp) {
                "+" -> operand1 + currentVal
                "-" -> operand1 - currentVal
                "×" -> operand1 * currentVal
                "÷" -> if (currentVal != 0.0) operand1 / currentVal else 0.0
                else -> currentVal
            }
            display = if (res % 1.0 == 0.0) res.toLong().toString() else "%.2f".format(res)
            pendingOp = null
            newNumberStarting = true
        }
    }

    fun onClear() {
        display = "0"
        operand1 = 0.0
        pendingOp = null
        newNumberStarting = false
    }

    fun onBackspace() {
        if (display.length > 1) {
            display = display.substring(0, display.length - 1)
        } else {
            display = "0"
        }
    }

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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.get("calculator", language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Display Screen
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (pendingOp != null) "${if (operand1 % 1.0 == 0.0) operand1.toLong().toString() else "%.2f".format(operand1)} $pendingOp" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "৳ $display",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calculator Keypad
                val buttons = listOf(
                    listOf("C", "÷", "×", "⌫"),
                    listOf("7", "8", "9", "-"),
                    listOf("4", "5", "6", "+"),
                    listOf("1", "2", "3", "="),
                    listOf("0", "00", ".", "=")
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    buttons.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            row.forEach { btn ->
                                val isOp = btn in listOf("+", "-", "×", "÷")
                                val isEq = btn == "="
                                val isClear = btn == "C" || btn == "⌫"

                                val bg = when {
                                    isEq -> MaterialTheme.colorScheme.primary
                                    isOp -> MaterialTheme.colorScheme.primaryContainer
                                    isClear -> MaterialTheme.colorScheme.errorContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }

                                val fg = when {
                                    isEq -> MaterialTheme.colorScheme.onPrimary
                                    isOp -> MaterialTheme.colorScheme.onPrimaryContainer
                                    isClear -> MaterialTheme.colorScheme.onErrorContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bg)
                                        .clickable {
                                            when (btn) {
                                                "C" -> onClear()
                                                "⌫" -> onBackspace()
                                                "=" -> onEquals()
                                                "+", "-", "×", "÷" -> onOp(btn)
                                                else -> onNum(btn)
                                            }
                                        }
                                ) {
                                    if (btn == "⌫") {
                                        Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = fg)
                                    } else {
                                        Text(
                                            text = btn,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = fg
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onEquals()
                        val finalAmt = display.toDoubleOrNull() ?: 0.0
                        onApplyAmount(finalAmt)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language.uppercase() == "BN") "টাকা এনট্রি করুন (৳ $display)" else "Use Amount (৳ $display)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
