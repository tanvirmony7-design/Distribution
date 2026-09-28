package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.ProductEntity
import com.example.ui.localization.DistroStrings
import com.example.ui.theme.DistroAccentCyan
import com.example.ui.theme.DistroGreenAction
import com.example.ui.theme.DistroGreenLight
import com.example.ui.theme.DistroHeaderBlue
import com.example.ui.theme.DistroLightBlue
import com.example.ui.theme.DistroOrangeAction
import com.example.ui.theme.DistroRedAlert
import com.example.ui.theme.DistroRedLight
import com.example.ui.theme.DistroTextPrimary
import com.example.ui.theme.DistroTextSecondary
import com.example.viewmodel.DistroViewModel
import com.example.viewmodel.StockFilter
import java.util.Locale

@Composable
fun ProductsScreen(
    viewModel: DistroViewModel,
    strings: DistroStrings,
    products: List<ProductEntity>,
    modifier: Modifier = Modifier
) {
    val searchQuery = viewModel.productSearchQuery.value
    val stockFilter = viewModel.productStockFilter.value
    val showAddProductDialog = viewModel.showAddProductDialog.value

    val formatCurrency = { amt: Double ->
        String.format(Locale.US, "৳ %,.2f", amt)
    }

    val filteredProducts = products.filter { prod ->
        val matchesQuery = if (searchQuery.isBlank()) true else {
            prod.name.contains(searchQuery, ignoreCase = true) ||
            prod.sku.contains(searchQuery, ignoreCase = true) ||
            prod.barcode.contains(searchQuery, ignoreCase = true) ||
            prod.category.contains(searchQuery, ignoreCase = true)
        }

        val matchesStock = when (stockFilter) {
            StockFilter.ALL -> true
            StockFilter.NEWEST -> true
            StockFilter.HIGH -> prod.stockQty > 40
            StockFilter.LOW -> prod.stockQty <= prod.minStockAlert
            StockFilter.ONLY_LOW -> prod.stockQty <= prod.minStockAlert
        }

        matchesQuery && matchesStock
    }

    val totalStockQty = filteredProducts.sumOf { it.stockQty }
    val totalBuyValue = filteredProducts.sumOf { it.buyPrice * it.stockQty }
    val totalSaleValue = filteredProducts.sumOf { it.sellPrice * it.stockQty }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("products_screen")
    ) {
        // Top Search, Barcode Scanner simulation & Filter Chips
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.productSearchQuery.value = it },
                        placeholder = { Text(text = strings.searchProductPlaceholder, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = DistroTextSecondary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.productSearchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("product_search_input")
                    )

                    // Barcode Scanner button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DistroHeaderBlue,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("barcode_scanner_button")
                    ) {
                        IconButton(onClick = {
                            // Simulate barcode scanner scan
                            val randomProd = products.randomOrNull()
                            if (randomProd != null) {
                                viewModel.productSearchQuery.value = randomProd.barcode
                                viewModel.showToast("Barcode Scanned: ${randomProd.barcode} (${randomProd.name})")
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan Barcode",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stock Filter Chips: All, Newest, High Stock, Low Stock, Only Low
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProductFilterChip(
                        title = strings.filterAll,
                        selected = stockFilter == StockFilter.ALL,
                        onClick = { viewModel.productStockFilter.value = StockFilter.ALL }
                    )
                    ProductFilterChip(
                        title = "Newest",
                        selected = stockFilter == StockFilter.NEWEST,
                        onClick = { viewModel.productStockFilter.value = StockFilter.NEWEST }
                    )
                    ProductFilterChip(
                        title = strings.filterHighStock,
                        selected = stockFilter == StockFilter.HIGH,
                        onClick = { viewModel.productStockFilter.value = StockFilter.HIGH }
                    )
                    ProductFilterChip(
                        title = strings.filterLowStock,
                        selected = stockFilter == StockFilter.LOW,
                        activeColor = DistroRedAlert,
                        onClick = { viewModel.productStockFilter.value = StockFilter.LOW }
                    )
                    ProductFilterChip(
                        title = strings.filterOnlyLow,
                        selected = stockFilter == StockFilter.ONLY_LOW,
                        activeColor = DistroOrangeAction,
                        onClick = { viewModel.productStockFilter.value = StockFilter.ONLY_LOW }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons: "Add Product +" and "Export PDF"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.showAddProductDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DistroGreenAction),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("add_product_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Product",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.addProductBtn,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.showToast("Stock Audit Report exported to PDF!")
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("export_products_pdf_button")
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

        // Product Cards List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No products found.",
                            color = DistroTextSecondary
                        )
                    }
                }
            }

            items(filteredProducts) { product ->
                val isLowStock = product.stockQty <= product.minStockAlert
                val marginAmt = product.sellPrice - product.buyPrice
                val marginPercent = if (product.buyPrice > 0) (marginAmt / product.buyPrice) * 100 else 0.0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_card_${product.sku}"),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DistroTextPrimary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = DistroLightBlue.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = product.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = DistroHeaderBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${product.sku} | Barcode: ${product.barcode}",
                                        fontSize = 10.sp,
                                        color = DistroTextSecondary
                                    )
                                }
                            }

                            // Stock Badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isLowStock) DistroRedLight else DistroGreenLight
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isLowStock) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Low",
                                            tint = DistroRedAlert,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = "${product.stockQty} ${product.unit}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLowStock) DistroRedAlert else DistroGreenAction
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Price & Valuation Breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Buy: ${formatCurrency(product.buyPrice)}",
                                    fontSize = 11.sp,
                                    color = DistroTextSecondary
                                )
                                Text(
                                    text = "Sell: ${formatCurrency(product.sellPrice)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DistroHeaderBlue
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Margin",
                                    fontSize = 10.sp,
                                    color = DistroTextSecondary
                                )
                                Text(
                                    text = String.format(Locale.US, "+%.1f%%", marginPercent),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DistroGreenAction
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Stock Value",
                                    fontSize = 10.sp,
                                    color = DistroTextSecondary
                                )
                                Text(
                                    text = formatCurrency(product.sellPrice * product.stockQty),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DistroTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Summary Footer: Total Products, Stock Quantity, Total Buy Value, Total Sale Value
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("products_footer_summary")
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
                            text = strings.totalProducts,
                            fontSize = 11.sp,
                            color = DistroTextSecondary
                        )
                        Text(
                            text = "${filteredProducts.size} SKUs",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroTextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = strings.stockQuantity,
                            fontSize = 11.sp,
                            color = DistroTextSecondary
                        )
                        Text(
                            text = "$totalStockQty Units",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroHeaderBlue
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = strings.totalSaleValue,
                            fontSize = 11.sp,
                            color = DistroTextSecondary
                        )
                        Text(
                            text = formatCurrency(totalSaleValue),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DistroGreenAction
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${strings.totalBuyValue}: ${formatCurrency(totalBuyValue)}",
                        fontSize = 11.sp,
                        color = DistroTextSecondary
                    )

                    val potentialProfit = totalSaleValue - totalBuyValue
                    Text(
                        text = "Potential Profit: ${formatCurrency(potentialProfit)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DistroGreenAction
                    )
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        AddProductDialog(
            viewModel = viewModel,
            strings = strings,
            onDismiss = { viewModel.showAddProductDialog.value = false }
        )
    }
}

@Composable
fun ProductFilterChip(
    title: String,
    selected: Boolean,
    activeColor: Color = DistroHeaderBlue,
    onClick: () -> Unit
) {
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

@Composable
fun AddProductDialog(
    viewModel: DistroViewModel,
    strings: DistroStrings,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Groceries") }
    var sku by remember { mutableStateOf("PRD-${System.currentTimeMillis() % 10000}") }
    var barcode by remember { mutableStateOf("8941" + (1000000..9999999).random()) }
    var unit by remember { mutableStateOf("Box") }
    var buyPriceText by remember { mutableStateOf("") }
    var sellPriceText by remember { mutableStateOf("") }
    var stockQtyText by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
                .testTag("add_product_dialog"),
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
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = "Add Product",
                            tint = DistroGreenAction,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.addProductBtn,
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

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name (e.g. Pran Frooto 250ml)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (Box/Can/Kg)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("SKU") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Barcode") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = buyPriceText,
                        onValueChange = { buyPriceText = it },
                        label = { Text("Buy Rate (৳)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sellPriceText,
                        onValueChange = { sellPriceText = it },
                        label = { Text("Sell Rate (৳)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stockQtyText,
                        onValueChange = { stockQtyText = it },
                        label = { Text("Stock Qty") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val buyPrice = buyPriceText.toDoubleOrNull() ?: 0.0
                        val sellPrice = sellPriceText.toDoubleOrNull() ?: 0.0
                        val stockQty = stockQtyText.toIntOrNull() ?: 0
                        if (name.isNotBlank() && sellPrice > 0) {
                            viewModel.addNewProduct(
                                name = name,
                                category = category,
                                sku = sku,
                                barcode = barcode,
                                unit = unit,
                                buyPrice = buyPrice,
                                sellPrice = sellPrice,
                                initialStock = stockQty
                            )
                        } else {
                            viewModel.showToast("Please provide product name and sell price.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DistroGreenAction),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_add_product_button")
                ) {
                    Text("Save Product to Catalog", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
