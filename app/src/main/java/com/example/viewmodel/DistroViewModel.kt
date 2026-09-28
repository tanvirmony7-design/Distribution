package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CartItem
import com.example.data.CustomerEntity
import com.example.data.DistributionRepository
import com.example.data.DistributionRouteEntity
import com.example.data.ExpenseEntity
import com.example.data.ProductEntity
import com.example.data.PurchaseInvoiceEntity
import com.example.data.ReturnRecordEntity
import com.example.data.SaleInvoiceEntity
import com.example.data.SupplierEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.BengaliStrings
import com.example.ui.localization.DistroStrings
import com.example.ui.localization.EnglishStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class NavigationTab {
    DASHBOARD,
    SALES,
    PURCHASE,
    PRODUCTS,
    REPORTS,
    ROUTES,
    CUSTOMERS,
    EXPENSES
}

enum class TimeFilter {
    ALL, TODAY, MONTH, YEAR
}

enum class StatusFilter {
    ALL, DUE, PAID
}

enum class StockFilter {
    ALL, NEWEST, HIGH, LOW, ONLY_LOW
}

class DistroViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = DistributionRepository(db)

    // Language
    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    val strings: StateFlow<DistroStrings> = combine(_language) { lang ->
        when (lang[0]) {
            AppLanguage.BENGALI -> BengaliStrings
            else -> EnglishStrings
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EnglishStrings)

    // Active Navigation
    private val _currentTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    // Base Data Flows from Room
    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suppliers: StateFlow<List<SupplierEntity>> = repository.allSuppliers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sales: StateFlow<List<SaleInvoiceEntity>> = repository.allSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val purchases: StateFlow<List<PurchaseInvoiceEntity>> = repository.allPurchases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routes: StateFlow<List<DistributionRouteEntity>> = repository.allRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val returns: StateFlow<List<ReturnRecordEntity>> = repository.allReturns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sales Module Filter & Search
    val salesSearchQuery = MutableStateFlow("")
    val salesTimeFilter = MutableStateFlow(TimeFilter.ALL)
    val salesStatusFilter = MutableStateFlow(StatusFilter.ALL)
    val salesPageLimit = MutableStateFlow(50)

    // Purchase Module Filter & Search
    val purchaseSearchQuery = MutableStateFlow("")
    val purchaseStatusFilter = MutableStateFlow(StatusFilter.ALL)

    // Product Module Filter & Search
    val productSearchQuery = MutableStateFlow("")
    val productStockFilter = MutableStateFlow(StockFilter.ALL)

    // Sync state
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Modals / Dialogs
    val showNewSaleDialog = MutableStateFlow(false)
    val showNewPurchaseDialog = MutableStateFlow(false)
    val showAddProductDialog = MutableStateFlow(false)
    val showAddCustomerDialog = MutableStateFlow(false)
    val showAddSupplierDialog = MutableStateFlow(false)
    val showAddExpenseDialog = MutableStateFlow(false)
    val showAddRouteDialog = MutableStateFlow(false)
    val selectedInvoiceForReceipt = MutableStateFlow<SaleInvoiceEntity?>(null)

    // Cart for creating new sale
    val saleCart = MutableStateFlow<List<CartItem>>(emptyList())
    val selectedCustomerId = MutableStateFlow<Int?>(null)
    val saleDiscount = MutableStateFlow(0.0)
    val salePaidAmount = MutableStateFlow(0.0)
    val salePaymentMode = MutableStateFlow("Cash")

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) {
            AppLanguage.BENGALI
        } else {
            AppLanguage.ENGLISH
        }
    }

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun triggerQuickSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1200) // Realistic cloud synchronization feel
            _isSyncing.value = false
            _toastMessage.value = if (_language.value == AppLanguage.BENGALI) {
                "ক্লাউড সার্ভারের সাথে সফলভাবে সিঙ্ক সম্পন্ন হয়েছে!"
            } else {
                "Synced successfully with Cloud Distribution Server!"
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    // New Sale Cart Actions
    fun addToSaleCart(product: ProductEntity) {
        val current = saleCart.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.productId == product.id }
        if (existingIndex >= 0) {
            val item = current[existingIndex]
            current[existingIndex] = item.copy(quantity = item.quantity + 1)
        } else {
            current.add(CartItem(
                productId = product.id,
                productName = product.name,
                unit = product.unit,
                unitPrice = product.sellPrice,
                quantity = 1
            ))
        }
        saleCart.value = current
    }

    fun updateCartQuantity(productId: Int, delta: Int) {
        val current = saleCart.value.toMutableList()
        val index = current.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            val item = current[index]
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                current.removeAt(index)
            } else {
                current[index] = item.copy(quantity = newQty)
            }
            saleCart.value = current
        }
    }

    fun removeCartItem(productId: Int) {
        saleCart.value = saleCart.value.filter { it.productId != productId }
    }

    fun clearCart() {
        saleCart.value = emptyList()
        selectedCustomerId.value = null
        saleDiscount.value = 0.0
        salePaidAmount.value = 0.0
        salePaymentMode.value = "Cash"
    }

    fun submitNewSale(onSuccess: (SaleInvoiceEntity) -> Unit) {
        val items = saleCart.value
        val custId = selectedCustomerId.value
        if (items.isEmpty() || custId == null) {
            showToast(if (_language.value == AppLanguage.BENGALI) "দয়া করে গ্রাহক ও পণ্য নির্বাচন করুন" else "Please select customer and items")
            return
        }
        val customer = customers.value.find { it.id == custId } ?: return

        val subtotal = items.sumOf { it.total }
        val discount = saleDiscount.value
        val grandTotal = (subtotal - discount).coerceAtLeast(0.0)
        val paid = salePaidAmount.value.coerceAtMost(grandTotal)
        val due = (grandTotal - paid).coerceAtLeast(0.0)

        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        val invoiceNo = "INV-" + (now % 1000000).toString()

        val summary = items.joinToString(", ") { "${it.productName} (${it.quantity})" }

        val newInvoice = SaleInvoiceEntity(
            invoiceNumber = invoiceNo,
            customerId = customer.id,
            customerName = customer.shopName,
            customerPhone = customer.phone,
            routeName = customer.routeName,
            dateTimestamp = now,
            dateFormatted = sdf.format(Date(now)),
            itemsSummary = summary,
            itemCount = items.sumOf { it.quantity },
            subtotal = subtotal,
            discount = discount,
            grandTotal = grandTotal,
            paidAmount = paid,
            dueAmount = due,
            paymentMode = salePaymentMode.value,
            deliveryStatus = "TAKEN",
            repName = "Tanvir Ahmed"
        )

        viewModelScope.launch {
            repository.recordSale(newInvoice, items)
            clearCart()
            showNewSaleDialog.value = false
            showToast(if (_language.value == AppLanguage.BENGALI) "চালান $invoiceNo তৈরি সম্পন্ন!" else "Invoice $invoiceNo created!")
            onSuccess(newInvoice)
        }
    }

    fun submitNewPurchase(
        supplierId: Int,
        supplierName: String,
        description: String,
        grandTotal: Double,
        paid: Double
    ) {
        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        val purchaseNo = "PUR-" + (now % 1000000).toString()
        val due = (grandTotal - paid).coerceAtLeast(0.0)

        val newPurchase = PurchaseInvoiceEntity(
            purchaseNumber = purchaseNo,
            supplierId = supplierId,
            supplierName = supplierName,
            dateTimestamp = now,
            dateFormatted = sdf.format(Date(now)),
            itemsSummary = description,
            grandTotal = grandTotal,
            paidAmount = paid,
            dueAmount = due
        )

        viewModelScope.launch {
            repository.recordPurchase(newPurchase)
            showNewPurchaseDialog.value = false
            showToast(if (_language.value == AppLanguage.BENGALI) "ক্রয় রেকর্ড সম্পন্ন!" else "Purchase recorded successfully!")
        }
    }

    fun advanceDeliveryStage(invoice: SaleInvoiceEntity) {
        viewModelScope.launch {
            repository.advanceDeliveryStatus(invoice.id, invoice.deliveryStatus)
            val nextStatus = when (invoice.deliveryStatus) {
                "TAKEN" -> "PACKED"
                "PACKED" -> "IN_TRANSIT"
                "IN_TRANSIT" -> "DELIVERED"
                else -> "DELIVERED"
            }
            if (selectedInvoiceForReceipt.value?.id == invoice.id) {
                selectedInvoiceForReceipt.value = invoice.copy(deliveryStatus = nextStatus)
            }
            showToast("Delivery status updated to $nextStatus")
        }
    }

    fun addNewProduct(
        name: String,
        category: String,
        sku: String,
        barcode: String,
        unit: String,
        buyPrice: Double,
        sellPrice: Double,
        initialStock: Int
    ) {
        viewModelScope.launch {
            val product = ProductEntity(
                name = name,
                category = category,
                sku = sku,
                barcode = barcode,
                unit = unit,
                buyPrice = buyPrice,
                sellPrice = sellPrice,
                stockQty = initialStock
            )
            repository.insertProduct(product)
            showAddProductDialog.value = false
            showToast("Product added successfully!")
        }
    }

    fun addNewCustomer(
        shopName: String,
        ownerName: String,
        phone: String,
        address: String,
        routeName: String,
        creditLimit: Double
    ) {
        viewModelScope.launch {
            val customer = CustomerEntity(
                shopName = shopName,
                ownerName = ownerName,
                phone = phone,
                address = address,
                routeName = routeName,
                totalDue = 0.0,
                creditLimit = creditLimit
            )
            repository.insertCustomer(customer)
            showAddCustomerDialog.value = false
            showToast("Customer store registered!")
        }
    }

    fun addNewRoute(
        routeName: String,
        repName: String,
        repPhone: String,
        vehicleNo: String
    ) {
        viewModelScope.launch {
            val route = DistributionRouteEntity(
                routeName = routeName,
                assignedRepName = repName,
                repPhone = repPhone,
                assignedShopsCount = 12,
                pendingDeliveriesCount = 0,
                vehicleNo = vehicleNo
            )
            repository.insertRoute(route)
            showAddRouteDialog.value = false
            showToast("New delivery route added!")
        }
    }

    fun addNewExpense(category: String, description: String, amount: Double) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
            val exp = ExpenseEntity(
                category = category,
                description = description,
                amount = amount,
                dateFormatted = sdf.format(Date(now)),
                dateTimestamp = now
            )
            repository.recordExpense(exp)
            showAddExpenseDialog.value = false
            showToast("Expense recorded successfully!")
        }
    }

    fun insertSupplier(supplier: SupplierEntity) {
        viewModelScope.launch {
            repository.insertSupplier(supplier)
            showAddSupplierDialog.value = false
            showToast("Supplier registered successfully!")
        }
    }

    fun recordInvoicePayment(invoice: SaleInvoiceEntity, paymentAmount: Double) {
        viewModelScope.launch {
            repository.recordPayment(invoice.id, invoice.customerId, paymentAmount)
            if (selectedInvoiceForReceipt.value?.id == invoice.id) {
                val newPaid = invoice.paidAmount + paymentAmount
                val newDue = (invoice.dueAmount - paymentAmount).coerceAtLeast(0.0)
                selectedInvoiceForReceipt.value = invoice.copy(paidAmount = newPaid, dueAmount = newDue)
            }
            showToast("Payment recorded!")
        }
    }
}
