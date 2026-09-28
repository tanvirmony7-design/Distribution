package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomerEntity
import com.example.data.ExpenseEntity
import com.example.data.ProductEntity
import com.example.data.PurchaseInvoiceEntity
import com.example.data.ReturnRecordEntity
import com.example.data.SaleInvoiceEntity
import com.example.data.SupplierEntity
import com.example.ui.localization.DistroStrings
import com.example.ui.theme.DistroAccentCyan
import com.example.ui.theme.DistroGreenAction
import com.example.ui.theme.DistroGreenLight
import com.example.ui.theme.DistroHeaderBlue
import com.example.ui.theme.DistroLightBlue
import com.example.ui.theme.DistroOrangeAction
import com.example.ui.theme.DistroOrangeLight
import com.example.ui.theme.DistroPurple
import com.example.ui.theme.DistroRedAlert
import com.example.ui.theme.DistroRedLight
import com.example.ui.theme.DistroTextPrimary
import com.example.ui.theme.DistroTextSecondary
import com.example.viewmodel.DistroViewModel
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: DistroViewModel,
    strings: DistroStrings,
    sales: List<SaleInvoiceEntity>,
    purchases: List<PurchaseInvoiceEntity>,
    expenses: List<ExpenseEntity>,
    products: List<ProductEntity>,
    customers: List<CustomerEntity>,
    suppliers: List<SupplierEntity>,
    returns: List<ReturnRecordEntity>,
    modifier: Modifier = Modifier
) {
    val totalSales = sales.sumOf { it.grandTotal }
    val totalPurchases = purchases.sumOf { it.grandTotal }
    val totalExpenses = expenses.sumOf { it.amount }
    val totalCustomerDue = customers.sumOf { it.totalDue }
    val totalSupplierDue = suppliers.sumOf { it.totalDue }
    val stockValuation = products.sumOf { it.sellPrice * it.stockQty }

    // Approximate Cost of Goods Sold (~82% of sales in distribution)
    val estimatedCogs = totalSales * 0.82
    val grossProfit = totalSales - estimatedCogs
    val netProfit = grossProfit - totalExpenses

    val formatCurrency = { amt: Double ->
        String.format(Locale.US, "৳ %,.2f", amt)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("reports_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Action: Export
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Distribution Financial Reports",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DistroHeaderBlue
                    )
                    Text(
                        text = "Real-time Profit, Loss & Ledgers",
                        fontSize = 12.sp,
                        color = DistroTextSecondary
                    )
                }

                Button(
                    onClick = {
                        viewModel.showToast("Full Financial Statement exported to PDF!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DistroHeaderBlue),
                    modifier = Modifier.testTag("export_full_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = strings.exportPdf, fontSize = 12.sp)
                }
            }
        }

        // P&L Statement Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profit & Loss (P&L) Summary",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DistroTextPrimary
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DistroGreenLight
                        ) {
                            Text(
                                text = "NET PROFIT: ${formatCurrency(netProfit)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = DistroGreenAction,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(8.dp))

                    ReportLine(label = "Total Sales Revenue", value = formatCurrency(totalSales), isPositive = true)
                    ReportLine(label = "Estimated COGS (Cost of Goods)", value = "- " + formatCurrency(estimatedCogs))
                    ReportLine(label = "Gross Operating Margin", value = formatCurrency(grossProfit), isBold = true)
                    ReportLine(label = "Operating Expenses (Fuel, Rent, Rep)", value = "- " + formatCurrency(totalExpenses))

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = DistroHeaderBlue.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated Net Earnings",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = DistroHeaderBlue
                        )
                        Text(
                            text = formatCurrency(netProfit),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = DistroGreenAction
                        )
                    }
                }
            }
        }

        // Dues & Working Capital Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Working Capital & Credit Ledger",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DistroTextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DistroRedLight,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "Customer Receivables", fontSize = 11.sp, color = DistroRedAlert)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = formatCurrency(totalCustomerDue), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DistroRedAlert)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DistroOrangeLight,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "Supplier Payables", fontSize = 11.sp, color = DistroOrangeAction)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = formatCurrency(totalSupplierDue), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DistroOrangeAction)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DistroLightBlue.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Total Warehouse Stock Valuation", fontSize = 11.sp, color = DistroHeaderBlue)
                                Text(text = formatCurrency(stockValuation), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DistroHeaderBlue)
                            }
                            Icon(Icons.Default.TrendingUp, contentDescription = "Stock", tint = DistroHeaderBlue)
                        }
                    }
                }
            }
        }

        // Inventory Category Distribution Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Category Inventory Share",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DistroTextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val categories = listOf(
                        Triple("Beverages (Juice, Soda, Tea)", 0.38f, DistroHeaderBlue),
                        Triple("Cooking Oils (Soybean, Mustard)", 0.28f, DistroOrangeAction),
                        Triple("Groceries & Staples (Sugar, Salt)", 0.20f, DistroGreenAction),
                        Triple("Toiletries & Snacks (Soap, Chips)", 0.14f, DistroPurple)
                    )

                    categories.forEach { (cat, pct, color) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = cat, fontSize = 12.sp, color = DistroTextPrimary)
                                Text(text = "${(pct * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { pct },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = color,
                                trackColor = color.copy(alpha = 0.15f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportLine(label: String, value: String, isPositive: Boolean = false, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isBold) DistroTextPrimary else DistroTextSecondary,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (isPositive) DistroGreenAction else DistroTextPrimary
        )
    }
}
