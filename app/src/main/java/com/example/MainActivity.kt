package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DistroBottomNavBar
import com.example.ui.components.ThermalReceiptDialog
import com.example.ui.components.TopAppBarHeader
import com.example.ui.screens.CustomerSupplierScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.PurchaseScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.RoutesDeliveryScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DistroViewModel
import com.example.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DistroApp()
            }
        }
    }
}

@Composable
fun DistroApp(viewModel: DistroViewModel = viewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val strings by viewModel.strings.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    val products by viewModel.products.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val purchases by viewModel.purchases.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val routes by viewModel.routes.collectAsStateWithLifecycle()
    val returns by viewModel.returns.collectAsStateWithLifecycle()

    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val selectedInvoiceForReceipt by viewModel.selectedInvoiceForReceipt.collectAsStateWithLifecycle()

    // Handle toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Back button handling
    BackHandler(enabled = currentTab != NavigationTab.DASHBOARD) {
        viewModel.setTab(NavigationTab.DASHBOARD)
    }

    val pendingDeliveriesCount = sales.count { it.deliveryStatus != "DELIVERED" }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBarHeader(
                strings = strings,
                currentLanguage = language,
                isSyncing = isSyncing,
                onSyncClick = { viewModel.triggerQuickSync() },
                onLanguageToggle = { viewModel.toggleLanguage() },
                onNavigateToRoutes = { viewModel.setTab(NavigationTab.ROUTES) }
            )
        },
        bottomBar = {
            DistroBottomNavBar(
                currentTab = currentTab,
                strings = strings,
                pendingDeliveriesCount = pendingDeliveriesCount,
                onTabSelected = { tab -> viewModel.setTab(tab) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.DASHBOARD -> {
                    DashboardScreen(
                        strings = strings,
                        sales = sales,
                        products = products,
                        customers = customers,
                        suppliers = suppliers,
                        expenses = expenses,
                        returns = returns,
                        onNavigateToTab = { tab -> viewModel.setTab(tab) },
                        onInvoiceClick = { invoice ->
                            viewModel.selectedInvoiceForReceipt.value = invoice
                        },
                        onNewSaleClick = {
                            viewModel.setTab(NavigationTab.SALES)
                            viewModel.showNewSaleDialog.value = true
                        },
                        onNewPurchaseClick = {
                            viewModel.setTab(NavigationTab.PURCHASE)
                            viewModel.showNewPurchaseDialog.value = true
                        }
                    )
                }

                NavigationTab.SALES -> {
                    SalesScreen(
                        viewModel = viewModel,
                        strings = strings,
                        sales = sales,
                        customers = customers,
                        products = products,
                        onInvoiceClick = { invoice ->
                            viewModel.selectedInvoiceForReceipt.value = invoice
                        }
                    )
                }

                NavigationTab.PURCHASE -> {
                    PurchaseScreen(
                        viewModel = viewModel,
                        strings = strings,
                        purchases = purchases,
                        suppliers = suppliers
                    )
                }

                NavigationTab.PRODUCTS -> {
                    ProductsScreen(
                        viewModel = viewModel,
                        strings = strings,
                        products = products
                    )
                }

                NavigationTab.REPORTS -> {
                    ReportsScreen(
                        viewModel = viewModel,
                        strings = strings,
                        sales = sales,
                        purchases = purchases,
                        expenses = expenses,
                        products = products,
                        customers = customers,
                        suppliers = suppliers,
                        returns = returns
                    )
                }

                NavigationTab.ROUTES -> {
                    RoutesDeliveryScreen(
                        viewModel = viewModel,
                        strings = strings,
                        routes = routes,
                        sales = sales,
                        onInvoiceClick = { invoice ->
                            viewModel.selectedInvoiceForReceipt.value = invoice
                        }
                    )
                }

                NavigationTab.CUSTOMERS -> {
                    CustomerSupplierScreen(
                        viewModel = viewModel,
                        strings = strings,
                        customers = customers,
                        suppliers = suppliers,
                        expenses = expenses,
                        initialTab = 0
                    )
                }

                NavigationTab.EXPENSES -> {
                    CustomerSupplierScreen(
                        viewModel = viewModel,
                        strings = strings,
                        customers = customers,
                        suppliers = suppliers,
                        expenses = expenses,
                        initialTab = 2
                    )
                }
            }
        }
    }

    // Thermal Receipt Dialog preview & printing
    selectedInvoiceForReceipt?.let { invoice ->
        ThermalReceiptDialog(
            invoice = invoice,
            strings = strings,
            onDismiss = { viewModel.selectedInvoiceForReceipt.value = null },
            onAdvanceDeliveryStatus = { inv ->
                viewModel.advanceDeliveryStage(inv)
            },
            onRecordPayment = { inv, amount ->
                viewModel.recordInvoicePayment(inv, amount)
            },
            onShowToast = { msg ->
                viewModel.showToast(msg)
            }
        )
    }
}
