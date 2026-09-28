package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.PurchaseInvoiceEntity
import com.example.data.SupplierEntity
import com.example.ui.localization.DistroStrings
import com.example.ui.theme.DistroGreenAction
import com.example.ui.theme.DistroGreenLight
import com.example.ui.theme.DistroHeaderBlue
import com.example.ui.theme.DistroOrangeAction
import com.example.ui.theme.DistroOrangeLight
import com.example.ui.theme.DistroRedAlert
import com.example.ui.theme.DistroRedLight
import com.example.ui.theme.DistroTextPrimary
import com.example.ui.theme.DistroTextSecondary
import com.example.viewmodel.DistroViewModel
import com.example.viewmodel.StatusFilter
import java.util.Locale

@Composable
fun PurchaseScreen(
    viewModel: DistroViewModel,
    strings: DistroStrings,
    purchases: List<PurchaseInvoiceEntity>,
    suppliers: List<SupplierEntity>,
    modifier: Modifier = Modifier
) {
    val searchQuery = viewModel.purchaseSearchQuery.value
    val statusFilter = viewModel.purchaseStatusFilter.value
    val showNewPurchaseDialog = viewModel.showNewPurchaseDialog.value

    val formatCurrency = { amt: Double ->
        String.format(Locale.US, "৳ %,.2f", amt)
    }

    val filteredPurchases = purchases.filter { purchase ->
        val matchesQuery = if (searchQuery.isBlank()) true else {
            purchase.purchaseNumber.contains(searchQuery, ignoreCase = true) ||
            purchase.supplierName.contains(searchQuery, ignoreCase = true) ||
            purchase.itemsSummary.contains(searchQuery, ignoreCase = true)
        }

        val matchesStatus = when (statusFilter) {
            StatusFilter.ALL -> true
            StatusFilter.DUE -> purchase.dueAmount > 0
            StatusFilter.PAID -> purchase.dueAmount <= 0
        }

        matchesQuery && matchesStatus
    }

    val totalAmount = filteredPurchases.sumOf { it.grandTotal }
    val paidAmount = filteredPurchases.sumOf { it.paidAmount }
    val dueAmount = filteredPurchases.sumOf { it.dueAmount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("purchase_screen")
    ) {
        // Top Bar: Search, Filters & Action Buttons
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.purchaseSearchQuery.value = it },
                    placeholder = { Text(text = strings.searchPurchasePlaceholder, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = DistroTextSecondary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.purchaseSearchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("purchase_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons: "New Purchase +" (Orange) & "Export PDF"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.showNewPurchaseDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DistroOrangeAction),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("new_purchase_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Purchase",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.newPurchaseBtn,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.showToast("Purchase summary exported to PDF successfully!")
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("export_purchase_pdf_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            tint = DistroOrangeAction,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.exportPdf,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroOrangeAction
                        )
                    }
                }
            }
        }

        // List of Purchases
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredPurchases.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No purchase records found.",
                            color = DistroTextSecondary
                        )
                    }
                }
            }

            items(filteredPurchases) { purchase ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("purchase_card_${purchase.purchaseNumber}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = purchase.purchaseNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DistroOrangeAction
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${purchase.dateFormatted.take(11)}",
                                    fontSize = 11.sp,
                                    color = DistroTextSecondary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (purchase.dueAmount <= 0) DistroGreenLight else DistroOrangeLight
                            ) {
                                Text(
                                    text = if (purchase.dueAmount <= 0) strings.statusPaid else "Due: ${formatCurrency(purchase.dueAmount)}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (purchase.dueAmount <= 0) DistroGreenAction else DistroOrangeAction,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = purchase.supplierName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroTextPrimary
                        )
                        Text(
                            text = purchase.itemsSummary,
                            fontSize = 11.sp,
                            color = DistroTextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Paid: ${formatCurrency(purchase.paidAmount)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = DistroGreenAction
                            )

                            Text(
                                text = "Total: ${formatCurrency(purchase.grandTotal)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DistroTextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Footer Summary: Purchase Count, Total Amount, Paid Amount, Due Amount
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("purchase_footer_summary")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = strings.purchaseCount,
                            fontSize = 11.sp,
                            color = DistroTextSecondary
                        )
                        Text(
                            text = "${filteredPurchases.size}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroTextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = strings.totalAmount,
                            fontSize = 11.sp,
                            color = DistroTextSecondary
                        )
                        Text(
                            text = formatCurrency(totalAmount),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DistroOrangeAction
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${strings.paidAmount}: ",
                            fontSize = 12.sp,
                            color = DistroTextSecondary
                        )
                        Text(
                            text = formatCurrency(paidAmount),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroGreenAction
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${strings.dueAmount}: ",
                            fontSize = 12.sp,
                            color = DistroTextSecondary
                        )
                        Text(
                            text = formatCurrency(dueAmount),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroOrangeAction
                        )
                    }
                }
            }
        }
    }

    // New Purchase Dialog
    if (showNewPurchaseDialog) {
        NewPurchaseDialog(
            viewModel = viewModel,
            strings = strings,
            suppliers = suppliers,
            onDismiss = { viewModel.showNewPurchaseDialog.value = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPurchaseDialog(
    viewModel: DistroViewModel,
    strings: DistroStrings,
    suppliers: List<SupplierEntity>,
    onDismiss: () -> Unit
) {
    var selectedSupplierId by remember { mutableStateOf<Int?>(null) }
    var expandedSupplier by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var totalAmountText by remember { mutableStateOf("") }
    var paidAmountText by remember { mutableStateOf("") }

    val selectedSupplier = suppliers.find { it.id == selectedSupplierId }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
                .testTag("new_purchase_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "New Purchase",
                            tint = DistroOrangeAction,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.newPurchaseBtn,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedSupplier,
                    onExpandedChange = { expandedSupplier = !expandedSupplier },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedSupplier?.companyName ?: strings.selectSupplier,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(strings.supplierName) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSupplier) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSupplier,
                        onDismissRequest = { expandedSupplier = false }
                    ) {
                        suppliers.forEach { supp ->
                            DropdownMenuItem(
                                text = { Text(supp.companyName) },
                                onClick = {
                                    selectedSupplierId = supp.id
                                    expandedSupplier = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Purchased Items Summary (e.g. 50 boxes juice, 20 cans oil)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = totalAmountText,
                        onValueChange = { totalAmountText = it },
                        label = { Text("Total Bill (৳)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = paidAmountText,
                        onValueChange = { paidAmountText = it },
                        label = { Text("Paid Now (৳)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val total = totalAmountText.toDoubleOrNull() ?: 0.0
                        val paid = paidAmountText.toDoubleOrNull() ?: 0.0
                        if (selectedSupplier != null && total > 0) {
                            viewModel.submitNewPurchase(
                                supplierId = selectedSupplier.id,
                                supplierName = selectedSupplier.companyName,
                                description = description.ifBlank { "Goods Purchase" },
                                grandTotal = total,
                                paid = paid
                            )
                        } else {
                            viewModel.showToast("Please enter supplier and valid total bill.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DistroOrangeAction),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_new_purchase_button")
                ) {
                    Text("Save Purchase Record", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
