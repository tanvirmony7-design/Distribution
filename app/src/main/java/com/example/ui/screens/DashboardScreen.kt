package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomerEntity
import com.example.data.ExpenseEntity
import com.example.data.ProductEntity
import com.example.data.ReturnRecordEntity
import com.example.data.SaleInvoiceEntity
import com.example.data.SupplierEntity
import com.example.ui.localization.DistroStrings
import com.example.ui.theme.DistroAccentCyan
import com.example.ui.theme.DistroAmberAlert
import com.example.ui.theme.DistroAmberLight
import com.example.ui.theme.DistroCardBorder
import com.example.ui.theme.DistroGreenAction
import com.example.ui.theme.DistroGreenLight
import com.example.ui.theme.DistroHeaderBlue
import com.example.ui.theme.DistroLightBlue
import com.example.ui.theme.DistroOrangeAction
import com.example.ui.theme.DistroOrangeLight
import com.example.ui.theme.DistroPrimaryBlue
import com.example.ui.theme.DistroPurple
import com.example.ui.theme.DistroPurpleLight
import com.example.ui.theme.DistroRedAlert
import com.example.ui.theme.DistroRedLight
import com.example.ui.theme.DistroTextPrimary
import com.example.ui.theme.DistroTextSecondary
import com.example.viewmodel.NavigationTab
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    strings: DistroStrings,
    sales: List<SaleInvoiceEntity>,
    products: List<ProductEntity>,
    customers: List<CustomerEntity>,
    suppliers: List<SupplierEntity>,
    expenses: List<ExpenseEntity>,
    returns: List<ReturnRecordEntity>,
    onNavigateToTab: (NavigationTab) -> Unit,
    onInvoiceClick: (SaleInvoiceEntity) -> Unit,
    onNewSaleClick: () -> Unit,
    onNewPurchaseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSalesAmount = sales.sumOf { it.grandTotal }
    val totalCustomerDue = customers.sumOf { it.totalDue }
    val totalSupplierDue = suppliers.sumOf { it.totalDue }
    val salesReturnAmount = returns.filter { it.type == "SALES_RETURN" }.sumOf { it.amount }
    val supplierReturnAmount = returns.filter { it.type == "SUPPLIER_RETURN" }.sumOf { it.amount }
    val totalExpenseAmount = expenses.sumOf { it.amount }
    val currentStockValue = products.sumOf { it.sellPrice * it.stockQty }

    val formatCurrency = { amt: Double ->
        String.format(Locale.US, "৳ %,.2f", amt)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Overview Card (Total Sales, Comparison %, Visual Trend Chart)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_top_sales_card"),
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
                        Column {
                            Text(
                                text = strings.totalSales,
                                fontSize = 13.sp,
                                color = DistroTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatCurrency(totalSalesAmount),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DistroHeaderBlue
                            )
                        }

                        // Growth Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DistroGreenLight,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Growth",
                                    tint = DistroGreenAction,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.vsLastMonth,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DistroGreenAction
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Visual Chart Component: Weekly Sales Activity Trend
                    Text(
                        text = "Weekly Sales Trend (Sun - Sat)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DistroTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    WeeklySalesChart(
                        sales = sales,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                    )
                }
            }
        }

        // 2. Summary Cards Grid (6 Essential DMS Metrics)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Customer Due
                    SummaryMetricCard(
                        title = strings.customerDue,
                        value = formatCurrency(totalCustomerDue),
                        icon = Icons.Default.People,
                        color = DistroRedAlert,
                        bgColor = DistroRedLight,
                        modifier = Modifier.weight(1f),
                        testTag = "card_customer_due",
                        onClick = { onNavigateToTab(NavigationTab.CUSTOMERS) }
                    )

                    // Supplier Due
                    SummaryMetricCard(
                        title = strings.supplierDue,
                        value = formatCurrency(totalSupplierDue),
                        icon = Icons.Default.Business,
                        color = DistroAmberAlert,
                        bgColor = DistroAmberLight,
                        modifier = Modifier.weight(1f),
                        testTag = "card_supplier_due",
                        onClick = { onNavigateToTab(NavigationTab.CUSTOMERS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Sales Return
                    SummaryMetricCard(
                        title = strings.salesReturn,
                        value = formatCurrency(salesReturnAmount),
                        icon = Icons.Default.AssignmentReturn,
                        color = DistroPurple,
                        bgColor = DistroPurpleLight,
                        modifier = Modifier.weight(1f),
                        testTag = "card_sales_return"
                    )

                    // Supplier Return
                    SummaryMetricCard(
                        title = strings.supplierReturn,
                        value = formatCurrency(supplierReturnAmount),
                        icon = Icons.Default.AssignmentReturn,
                        color = DistroPrimaryBlue,
                        bgColor = DistroLightBlue.copy(alpha = 0.15f),
                        modifier = Modifier.weight(1f),
                        testTag = "card_supplier_return"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Expense
                    SummaryMetricCard(
                        title = strings.totalExpense,
                        value = formatCurrency(totalExpenseAmount),
                        icon = Icons.Default.MoneyOff,
                        color = DistroOrangeAction,
                        bgColor = DistroOrangeLight,
                        modifier = Modifier.weight(1f),
                        testTag = "card_total_expense",
                        onClick = { onNavigateToTab(NavigationTab.EXPENSES) }
                    )

                    // Current Stock Value
                    SummaryMetricCard(
                        title = strings.currentStockValue,
                        value = formatCurrency(currentStockValue),
                        icon = Icons.Default.Inventory2,
                        color = DistroGreenAction,
                        bgColor = DistroGreenLight,
                        modifier = Modifier.weight(1f),
                        testTag = "card_stock_value",
                        onClick = { onNavigateToTab(NavigationTab.PRODUCTS) }
                    )
                }
            }
        }

        // 3. Quick Access Grid
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_access_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = strings.quickShortcuts,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DistroTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        maxItemsInEachRow = 4,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionItem(
                            title = strings.actionSales,
                            icon = Icons.Default.PointOfSale,
                            color = DistroGreenAction,
                            onClick = onNewSaleClick
                        )
                        QuickActionItem(
                            title = strings.actionPurchase,
                            icon = Icons.Default.ShoppingBag,
                            color = DistroOrangeAction,
                            onClick = onNewPurchaseClick
                        )
                        QuickActionItem(
                            title = strings.actionCustomer,
                            icon = Icons.Default.People,
                            color = DistroPrimaryBlue,
                            onClick = { onNavigateToTab(NavigationTab.CUSTOMERS) }
                        )
                        QuickActionItem(
                            title = strings.actionSupplier,
                            icon = Icons.Default.Business,
                            color = DistroAmberAlert,
                            onClick = { onNavigateToTab(NavigationTab.CUSTOMERS) }
                        )
                        QuickActionItem(
                            title = strings.actionProducts,
                            icon = Icons.Default.Inventory2,
                            color = DistroAccentCyan,
                            onClick = { onNavigateToTab(NavigationTab.PRODUCTS) }
                        )
                        QuickActionItem(
                            title = strings.actionRoutes,
                            icon = Icons.Default.AltRoute,
                            color = DistroHeaderBlue,
                            onClick = { onNavigateToTab(NavigationTab.ROUTES) }
                        )
                        QuickActionItem(
                            title = strings.actionExpense,
                            icon = Icons.Default.MoneyOff,
                            color = DistroRedAlert,
                            onClick = { onNavigateToTab(NavigationTab.EXPENSES) }
                        )
                        QuickActionItem(
                            title = strings.actionReports,
                            icon = Icons.Default.Assessment,
                            color = DistroPurple,
                            onClick = { onNavigateToTab(NavigationTab.REPORTS) }
                        )
                    }
                }
            }
        }

        // 4. Recent Invoices Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.recentInvoices,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DistroTextPrimary
                )
                Text(
                    text = strings.viewAll,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DistroHeaderBlue,
                    modifier = Modifier
                        .clickable { onNavigateToTab(NavigationTab.SALES) }
                        .padding(4.dp)
                )
            }
        }

        items(sales.take(4)) { invoice ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onInvoiceClick(invoice) }
                    .testTag("invoice_item_${invoice.invoiceNumber}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DistroLightBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = "Invoice",
                                tint = DistroHeaderBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = invoice.invoiceNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DistroHeaderBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${invoice.dateFormatted.take(11)}",
                                    fontSize = 11.sp,
                                    color = DistroTextSecondary
                                )
                            }
                            Text(
                                text = invoice.customerName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = DistroTextPrimary
                            )
                            Text(
                                text = invoice.routeName,
                                fontSize = 11.sp,
                                color = DistroTextSecondary
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatCurrency(invoice.grandTotal),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DistroTextPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

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
}

@Composable
fun SummaryMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (onClick != null) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = DistroTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                color = DistroTextSecondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DistroTextPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = DistroTextPrimary,
            maxLines = 1
        )
    }
}

@Composable
fun WeeklySalesChart(
    sales: List<SaleInvoiceEntity>,
    modifier: Modifier = Modifier
) {
    // Generate 7 day values for visual trend
    val values = listOf(35000f, 48000f, 42000f, 59000f, 51000f, 68000f, 81600f)
    val labels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Today")
    val maxVal = values.maxOrNull() ?: 1f

    Canvas(modifier = modifier) {
        val barWidth = size.width / (values.size * 2)
        val spacing = size.width / values.size

        values.forEachIndexed { i, value ->
            val barHeight = (value / maxVal) * (size.height - 18.dp.toPx())
            val x = (i * spacing) + (spacing - barWidth) / 2
            val y = size.height - barHeight - 12.dp.toPx()

            // Bar with rounded top
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = if (i == values.lastIndex) {
                        listOf(Color(0xFF2563EB), Color(0xFF1E3A8A))
                    } else {
                        listOf(Color(0xFF93C5FD), Color(0xFF3B82F6))
                    }
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        labels.forEachIndexed { index, label ->
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = if (index == labels.lastIndex) FontWeight.Bold else FontWeight.Normal,
                color = if (index == labels.lastIndex) DistroHeaderBlue else DistroTextSecondary
            )
        }
    }
}
