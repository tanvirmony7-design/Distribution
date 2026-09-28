package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CartItem
import com.example.data.CustomerEntity
import com.example.data.ProductEntity
import com.example.data.SaleInvoiceEntity
import com.example.ui.localization.DistroStrings
import com.example.ui.theme.DistroGreenAction
import com.example.ui.theme.DistroGreenLight
import com.example.ui.theme.DistroHeaderBlue
import com.example.ui.theme.DistroLightBlue
import com.example.ui.theme.DistroOrangeAction
import com.example.ui.theme.DistroOrangeLight
import com.example.ui.theme.DistroRedAlert
import com.example.ui.theme.DistroRedLight
import com.example.ui.theme.DistroTextPrimary
import com.example.ui.theme.DistroTextSecondary
import com.example.viewmodel.DistroViewModel
import com.example.viewmodel.StatusFilter
import com.example.viewmodel.TimeFilter
import java.util.Locale

@Composable
fun SalesScreen(
    viewModel: DistroViewModel,
    strings: DistroStrings,
    sales: List<SaleInvoiceEntity>,
    customers: List<CustomerEntity>,
    products: List<ProductEntity>,
    onInvoiceClick: (SaleInvoiceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery = viewModel.salesSearchQuery.value
    val timeFilter = viewModel.salesTimeFilter.value
    val statusFilter = viewModel.salesStatusFilter.value
    val pageLimit = viewModel.salesPageLimit.value
    val showNewSaleDialog = viewModel.showNewSaleDialog.value

    val formatCurrency = { amt: Double ->
        String.format(Locale.US, "৳ %,.2f", amt)
    }

    // Filtered Invoices
    val filteredSales = sales.filter { invoice ->
        val matchesQuery = if (searchQuery.isBlank()) true else {
            invoice.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
            invoice.customerName.contains(searchQuery, ignoreCase = true) ||
            invoice.customerPhone.contains(searchQuery, ignoreCase = true) ||
            invoice.routeName.contains(searchQuery, ignoreCase = true)
        }

        val matchesStatus = when (statusFilter) {
            StatusFilter.ALL -> true
            StatusFilter.DUE -> invoice.dueAmount > 0
            StatusFilter.PAID -> invoice.dueAmount <= 0
        }

        matchesQuery && matchesStatus
    }.take(pageLimit)

    val totalAmount = filteredSales.sumOf { it.grandTotal }
    val paidAmount = filteredSales.sumOf { it.paidAmount }
    val dueAmount = filteredSales.sumOf { it.dueAmount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("sales_screen")
    ) {
        // Top Filter Bar & Search
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.salesSearchQuery.value = it },
                    placeholder = { Text(text = strings.searchSalePlaceholder, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = DistroTextSecondary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.salesSearchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("sales_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Filter Chips (Time, Status, Pagination Limit)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time filters
                    TimeFilterChip(
                        title = strings.filterAll,
                        selected = timeFilter == TimeFilter.ALL,
                        onClick = { viewModel.salesTimeFilter.value = TimeFilter.ALL }
                    )
                    TimeFilterChip(
                        title = strings.filterToday,
                        selected = timeFilter == TimeFilter.TODAY,
                        onClick = { viewModel.salesTimeFilter.value = TimeFilter.TODAY }
                    )
                    TimeFilterChip(
                        title = strings.filterThisMonth,
                        selected = timeFilter == TimeFilter.MONTH,
                        onClick = { viewModel.salesTimeFilter.value = TimeFilter.MONTH }
                    )
                    TimeFilterChip(
                        title = strings.filterThisYear,
                        selected = timeFilter == TimeFilter.YEAR,
                        onClick = { viewModel.salesTimeFilter.value = TimeFilter.YEAR }
                    )

                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(DistroTextSecondary.copy(alpha = 0.3f))
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    // Status filters
                    StatusFilterChip(
                        title = strings.filterDue,
                        selected = statusFilter == StatusFilter.DUE,
                        activeColor = DistroRedAlert,
                        onClick = {
                            viewModel.salesStatusFilter.value =
                                if (statusFilter == StatusFilter.DUE) StatusFilter.ALL else StatusFilter.DUE
                        }
                    )
                    StatusFilterChip(
                        title = strings.filterPaid,
                        selected = statusFilter == StatusFilter.PAID,
                        activeColor = DistroGreenAction,
                        onClick = {
                            viewModel.salesStatusFilter.value =
                                if (statusFilter == StatusFilter.PAID) StatusFilter.ALL else StatusFilter.PAID
                        }
                    )

                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(DistroTextSecondary.copy(alpha = 0.3f))
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    // Limit chips (20, 50, 100)
                    listOf(20, 50, 100).forEach { limit ->
                        FilterChip(
                            selected = pageLimit == limit,
                            onClick = { viewModel.salesPageLimit.value = limit },
                            label = { Text("$limit", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DistroLightBlue.copy(alpha = 0.2f),
                                selectedLabelColor = DistroHeaderBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons: "New Sale +" (Green) & "Export PDF"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.showNewSaleDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DistroGreenAction),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("new_sale_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Sale",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.newSaleBtn,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.showToast("Invoices Report exported to PDF format!")
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("export_sales_pdf_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            tint = DistroHeaderBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.exportPdf,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroHeaderBlue
                        )
                    }
                }
            }
        }

        // Invoice List Table / Cards
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredSales.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No invoices found for this filter.",
                            color = DistroTextSecondary
                        )
                    }
                }
            }

            items(filteredSales) { invoice ->
                InvoiceCardItem(
                    invoice = invoice,
                    strings = strings,
                    onClick = { onInvoiceClick(invoice) }
                )
            }
        }

        // Footer Summary: Invoice Count, Total Amount, Paid Amount, Due Amount
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sales_footer_summary")
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
                            text = strings.invoiceCount,
                            fontSize = 11.sp,
                            color = DistroTextSecondary
                        )
                        Text(
                            text = "${filteredSales.size}",
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
                            color = DistroHeaderBlue
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
                            color = DistroRedAlert
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog: New Sale Creation
    if (showNewSaleDialog) {
        NewSaleDialog(
            viewModel = viewModel,
            strings = strings,
            customers = customers,
            products = products,
            onDismiss = { viewModel.showNewSaleDialog.value = false },
            onInvoiceCreated = { onInvoiceClick(it) }
        )
    }
}

@Composable
fun InvoiceCardItem(
    invoice: SaleInvoiceEntity,
    strings: DistroStrings,
    onClick: () -> Unit
) {
    val formatCurrency = { amt: Double ->
        String.format(Locale.US, "৳ %,.2f", amt)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("invoice_card_${invoice.invoiceNumber}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Row 1: Invoice #, Date, Delivery Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = invoice.invoiceNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DistroHeaderBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${invoice.dateFormatted.take(11)}",
                        fontSize = 11.sp,
                        color = DistroTextSecondary
                    )
                }

                // Delivery badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (invoice.deliveryStatus) {
                        "DELIVERED" -> DistroGreenLight
                        "IN_TRANSIT" -> DistroOrangeLight
                        else -> DistroLightBlue.copy(alpha = 0.15f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Delivery",
                            tint = when (invoice.deliveryStatus) {
                                "DELIVERED" -> DistroGreenAction
                                "IN_TRANSIT" -> DistroOrangeAction
                                else -> DistroHeaderBlue
                            },
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (invoice.deliveryStatus) {
                                "TAKEN" -> strings.stageTaken
                                "PACKED" -> strings.stagePacked
                                "IN_TRANSIT" -> strings.stageInTransit
                                "DELIVERED" -> strings.stageDelivered
                                else -> invoice.deliveryStatus
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (invoice.deliveryStatus) {
                                "DELIVERED" -> DistroGreenAction
                                "IN_TRANSIT" -> DistroOrangeAction
                                else -> DistroHeaderBlue
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Row 2: Customer Name, Phone, Route
            Text(
                text = invoice.customerName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = DistroTextPrimary
            )
            Text(
                text = "${invoice.routeName} • Rep: ${invoice.repName}",
                fontSize = 11.sp,
                color = DistroTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(6.dp))

            // Row 3: Items count & Financials
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${invoice.itemCount} items",
                    fontSize = 11.sp,
                    color = DistroTextSecondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Total: ${formatCurrency(invoice.grandTotal)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DistroTextPrimary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (invoice.dueAmount <= 0) DistroGreenLight else DistroRedLight
                    ) {
                        Text(
                            text = if (invoice.dueAmount <= 0) strings.statusPaid else "Due: ${formatCurrency(invoice.dueAmount)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (invoice.dueAmount <= 0) DistroGreenAction else DistroRedAlert,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeFilterChip(title: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = title, fontSize = 11.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = DistroLightBlue.copy(alpha = 0.2f),
            selectedLabelColor = DistroHeaderBlue
        )
    )
}

@Composable
fun StatusFilterChip(title: String, selected: Boolean, activeColor: Color, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = title, fontSize = 11.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = activeColor.copy(alpha = 0.15f),
            selectedLabelColor = activeColor
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSaleDialog(
    viewModel: DistroViewModel,
    strings: DistroStrings,
    customers: List<CustomerEntity>,
    products: List<ProductEntity>,
    onDismiss: () -> Unit,
    onInvoiceCreated: (SaleInvoiceEntity) -> Unit
) {
    val cart = viewModel.saleCart.value
    val selectedCustomerId = viewModel.selectedCustomerId.value
    val discount = viewModel.saleDiscount.value
    val paid = viewModel.salePaidAmount.value
    val paymentMode = viewModel.salePaymentMode.value

    var expandedCustomerDropdown by remember { mutableStateOf(false) }
    var expandedProductDropdown by remember { mutableStateOf(false) }

    val subtotal = cart.sumOf { it.total }
    val grandTotal = (subtotal - discount).coerceAtLeast(0.0)
    val calculatedDue = (grandTotal - paid).coerceAtLeast(0.0)

    val formatCurrency = { amt: Double ->
        String.format(Locale.US, "৳ %,.2f", amt)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("new_sale_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PointOfSale,
                            contentDescription = "New Sale",
                            tint = DistroGreenAction,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.newSaleBtn,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Customer Selection Dropdown
                val selectedCustomer = customers.find { it.id == selectedCustomerId }

                ExposedDropdownMenuBox(
                    expanded = expandedCustomerDropdown,
                    onExpandedChange = { expandedCustomerDropdown = !expandedCustomerDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCustomer?.shopName ?: strings.selectCustomer,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(strings.customerName) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCustomerDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("select_customer_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCustomerDropdown,
                        onDismissRequest = { expandedCustomerDropdown = false }
                    ) {
                        customers.forEach { customer ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(customer.shopName, fontWeight = FontWeight.Bold)
                                        Text(
                                            "${customer.routeName} • Prev Due: ${formatCurrency(customer.totalDue)}",
                                            fontSize = 11.sp,
                                            color = DistroTextSecondary
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.selectedCustomerId.value = customer.id
                                    expandedCustomerDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Product Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedProductDropdown,
                    onExpandedChange = { expandedProductDropdown = !expandedProductDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = strings.selectProduct,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("+ Add Products to Cart") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProductDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("select_product_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = expandedProductDropdown,
                        onDismissRequest = { expandedProductDropdown = false }
                    ) {
                        products.forEach { prod ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(prod.name, fontWeight = FontWeight.Bold)
                                            Text(
                                                "Stock: ${prod.stockQty} ${prod.unit}",
                                                fontSize = 11.sp,
                                                color = if (prod.stockQty <= prod.minStockAlert) DistroRedAlert else DistroTextSecondary
                                            )
                                        }
                                        Text(
                                            formatCurrency(prod.sellPrice),
                                            fontWeight = FontWeight.Bold,
                                            color = DistroHeaderBlue
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.addToSaleCart(prod)
                                    expandedProductDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cart Line Items List
                Text(
                    text = "Cart Items (${cart.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DistroTextSecondary
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (cart.isEmpty()) {
                        item {
                            Text(
                                text = "Select products from the dropdown above to add items.",
                                fontSize = 12.sp,
                                color = DistroTextSecondary,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }

                    items(cart) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.productName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "${formatCurrency(item.unitPrice)} x ${item.quantity} = ${formatCurrency(item.total)}",
                                    fontSize = 11.sp,
                                    color = DistroTextSecondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.productId, -1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "-", modifier = Modifier.size(16.dp))
                                }
                                Text("${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.productId, 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "+", modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.removeCartItem(item.productId) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = DistroRedAlert,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Pricing Inputs: Discount, Paid, Payment Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (discount == 0.0) "" else discount.toString(),
                        onValueChange = {
                            viewModel.saleDiscount.value = it.toDoubleOrNull() ?: 0.0
                        },
                        label = { Text("Discount (৳)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = if (paid == 0.0) "" else paid.toString(),
                        onValueChange = {
                            viewModel.salePaidAmount.value = it.toDoubleOrNull() ?: 0.0
                        },
                        label = { Text("Paid Cash (৳)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Grand Total & Due Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Grand Total: ${formatCurrency(grandTotal)}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = DistroHeaderBlue)
                        Text(text = "Remaining Due: ${formatCurrency(calculatedDue)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (calculatedDue > 0) DistroRedAlert else DistroGreenAction)
                    }

                    Button(
                        onClick = {
                            viewModel.submitNewSale { createdInvoice ->
                                onInvoiceCreated(createdInvoice)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DistroGreenAction),
                        enabled = cart.isNotEmpty() && selectedCustomerId != null,
                        modifier = Modifier.testTag("submit_new_sale_button")
                    ) {
                        Text("Create & Print", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
