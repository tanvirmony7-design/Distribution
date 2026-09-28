package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DistributionRepository(private val db: AppDatabase) {
    val allProducts: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
    val allCustomers: Flow<List<CustomerEntity>> = db.customerDao().getAllCustomers()
    val allSuppliers: Flow<List<SupplierEntity>> = db.supplierDao().getAllSuppliers()
    val allSales: Flow<List<SaleInvoiceEntity>> = db.saleDao().getAllSales()
    val allPurchases: Flow<List<PurchaseInvoiceEntity>> = db.purchaseDao().getAllPurchases()
    val allExpenses: Flow<List<ExpenseEntity>> = db.expenseDao().getAllExpenses()
    val allRoutes: Flow<List<DistributionRouteEntity>> = db.routeDao().getAllRoutes()
    val allReturns: Flow<List<ReturnRecordEntity>> = db.returnDao().getAllReturns()

    suspend fun checkAndSeedInitialData() {
        val existingProducts = allProducts.firstOrNull()
        if (existingProducts.isNullOrEmpty()) {
            seedSampleData()
        }
    }

    private suspend fun seedSampleData() {
        val sampleProducts = listOf(
            ProductEntity(name = "Pran Frooto Mango 250ml", category = "Beverages", sku = "BEV-101", barcode = "89411002341", unit = "Box (24)", buyPrice = 420.0, sellPrice = 480.0, stockQty = 85, minStockAlert = 20),
            ProductEntity(name = "Teer Soybean Oil 5L", category = "Cooking Oil", sku = "OIL-201", barcode = "89412003452", unit = "Can", buyPrice = 780.0, sellPrice = 850.0, stockQty = 42, minStockAlert = 15),
            ProductEntity(name = "Radhuni Mustard Oil 1L", category = "Cooking Oil", sku = "OIL-202", barcode = "89412003463", unit = "Bottle", buyPrice = 310.0, sellPrice = 345.0, stockQty = 18, minStockAlert = 20),
            ProductEntity(name = "Fresh Refined Sugar 1kg", category = "Groceries", sku = "GRO-301", barcode = "89413004567", unit = "Bale (25kg)", buyPrice = 3200.0, sellPrice = 3450.0, stockQty = 12, minStockAlert = 10),
            ProductEntity(name = "ACI Pure Iodized Salt 1kg", category = "Groceries", sku = "GRO-302", barcode = "89413004588", unit = "Carton (30)", buyPrice = 960.0, sellPrice = 1080.0, stockQty = 60, minStockAlert = 25),
            ProductEntity(name = "Ispahani Mirzapore Tea 400g", category = "Beverages", sku = "BEV-102", barcode = "89411002399", unit = "Carton (20)", buyPrice = 4200.0, sellPrice = 4600.0, stockQty = 8, minStockAlert = 10),
            ProductEntity(name = "Bombay Sweets Potato Crackers", category = "Snacks", sku = "SNK-401", barcode = "89414005611", unit = "Carton (48)", buyPrice = 720.0, sellPrice = 840.0, stockQty = 95, minStockAlert = 30),
            ProductEntity(name = "Dettol Original Soap 75g", category = "Toiletries", sku = "TOI-501", barcode = "89415006722", unit = "Box (36)", buyPrice = 1440.0, sellPrice = 1620.0, stockQty = 34, minStockAlert = 15),
            ProductEntity(name = "Wheel 2-in-1 Powder 500g", category = "Toiletries", sku = "TOI-502", barcode = "89415006733", unit = "Carton (24)", buyPrice = 1150.0, sellPrice = 1320.0, stockQty = 14, minStockAlert = 20),
            ProductEntity(name = "Coca-Cola Pet Bottle 500ml", category = "Beverages", sku = "BEV-103", barcode = "89411002377", unit = "Crate (24)", buyPrice = 840.0, sellPrice = 960.0, stockQty = 110, minStockAlert = 25)
        )
        db.productDao().insertAll(sampleProducts)

        val sampleRoutes = listOf(
            DistributionRouteEntity(routeName = "Mirpur Sector 1-14 Route", assignedRepName = "Tanvir Ahmed", repPhone = "+8801711223344", assignedShopsCount = 28, pendingDeliveriesCount = 3, vehicleNo = "Dhaka-Metro-Ta-11-2041"),
            DistributionRouteEntity(routeName = "Dhanmondi & Green Road", assignedRepName = "Shakil Hossain", repPhone = "+8801812334455", assignedShopsCount = 34, pendingDeliveriesCount = 2, vehicleNo = "Dhaka-Metro-Ta-14-5512"),
            DistributionRouteEntity(routeName = "Uttara Commercial Area", assignedRepName = "Mahmudul Hasan", repPhone = "+8801913445566", assignedShopsCount = 22, pendingDeliveriesCount = 4, vehicleNo = "Dhaka-Metro-Ta-09-8812"),
            DistributionRouteEntity(routeName = "Old Dhaka Chawkbazar Hub", assignedRepName = "Kamrul Islam", repPhone = "+8801614556677", assignedShopsCount = 45, pendingDeliveriesCount = 5, vehicleNo = "Dhaka-Metro-Ta-18-3321")
        )
        db.routeDao().insertAll(sampleRoutes)

        val sampleCustomers = listOf(
            CustomerEntity(shopName = "Al-Madina General Store", ownerName = "Haji Rafiqul Islam", phone = "+8801712998877", address = "Shop #14, Mirpur-10 Circle", routeName = "Mirpur Sector 1-14 Route", totalDue = 14500.0, creditLimit = 60000.0),
            CustomerEntity(shopName = "Bismillah Super Shop", ownerName = "Abul Kashem", phone = "+8801819887766", address = "Road 27, Dhanmondi, Dhaka", routeName = "Dhanmondi & Green Road", totalDue = 28400.0, creditLimit = 80000.0),
            CustomerEntity(shopName = "Maa Grocery & Confectionery", ownerName = "Shahidul Alam", phone = "+8801918776655", address = "Sector 3, Uttara, Dhaka", routeName = "Uttara Commercial Area", totalDue = 8200.0, creditLimit = 40000.0),
            CustomerEntity(shopName = "Rahman Traders Wholesale", ownerName = "Matiur Rahman", phone = "+8801617665544", address = "Chawkbazar Main Road", routeName = "Old Dhaka Chawkbazar Hub", totalDue = 42500.0, creditLimit = 120000.0),
            CustomerEntity(shopName = "New Dhaka Mini Mart", ownerName = "Tariqul Islam", phone = "+8801716554433", address = "Section 2, Mirpur", routeName = "Mirpur Sector 1-14 Route", totalDue = 0.0, creditLimit = 50000.0)
        )
        db.customerDao().insertAll(sampleCustomers)

        val sampleSuppliers = listOf(
            SupplierEntity(companyName = "Pran-RFL Group Central Depo", contactPerson = "Fazle Rabbi (Depo Mgr)", phone = "+8801700112233", address = "Tejgaon I/A, Dhaka", totalDue = 85000.0),
            SupplierEntity(companyName = "City Group (Teer Brands)", contactPerson = "Moniruzzaman", phone = "+8801700223344", address = "Dillu Road, Dhaka", totalDue = 62000.0),
            SupplierEntity(companyName = "Square Consumer Products Ltd", contactPerson = "Zahidul Karim", phone = "+8801700334455", address = "Mohakhali C/A, Dhaka", totalDue = 34500.0),
            SupplierEntity(companyName = "Meghna Group of Industries (Fresh)", contactPerson = "Anisur Rahman", phone = "+8801700445566", address = "Gulshan-1, Dhaka", totalDue = 48000.0)
        )
        db.supplierDao().insertAll(sampleSuppliers)

        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)

        val sampleSales = listOf(
            SaleInvoiceEntity(
                invoiceNumber = "INV-2026-0891",
                customerId = 1,
                customerName = "Al-Madina General Store",
                customerPhone = "+8801712998877",
                routeName = "Mirpur Sector 1-14 Route",
                dateTimestamp = now - 3600000L * 3,
                dateFormatted = sdf.format(Date(now - 3600000L * 3)),
                itemsSummary = "Pran Frooto (10 Box), Teer Soybean Oil (5 Can)",
                itemCount = 15,
                subtotal = 9050.0,
                discount = 250.0,
                grandTotal = 8800.0,
                paidAmount = 5000.0,
                dueAmount = 3800.0,
                paymentMode = "Cash & Credit",
                deliveryStatus = "IN_TRANSIT",
                repName = "Tanvir Ahmed"
            ),
            SaleInvoiceEntity(
                invoiceNumber = "INV-2026-0890",
                customerId = 2,
                customerName = "Bismillah Super Shop",
                customerPhone = "+8801819887766",
                routeName = "Dhanmondi & Green Road",
                dateTimestamp = now - 3600000L * 7,
                dateFormatted = sdf.format(Date(now - 3600000L * 7)),
                itemsSummary = "Fresh Sugar (2 Bale), ACI Salt (4 Carton), Bombay Potato Crackers (5 Carton)",
                itemCount = 11,
                subtotal = 15420.0,
                discount = 420.0,
                grandTotal = 15000.0,
                paidAmount = 15000.0,
                dueAmount = 0.0,
                paymentMode = "bKash Digital",
                deliveryStatus = "DELIVERED",
                repName = "Shakil Hossain"
            ),
            SaleInvoiceEntity(
                invoiceNumber = "INV-2026-0889",
                customerId = 4,
                customerName = "Rahman Traders Wholesale",
                customerPhone = "+8801617665544",
                routeName = "Old Dhaka Chawkbazar Hub",
                dateTimestamp = now - 3600000L * 24,
                dateFormatted = sdf.format(Date(now - 3600000L * 24)),
                itemsSummary = "Ispahani Tea (4 Carton), Coca-Cola 500ml (10 Crate), Dettol Soap (6 Box)",
                itemCount = 20,
                subtotal = 37720.0,
                discount = 720.0,
                grandTotal = 37000.0,
                paidAmount = 25000.0,
                dueAmount = 12000.0,
                paymentMode = "Bank Check",
                deliveryStatus = "DELIVERED",
                repName = "Kamrul Islam"
            ),
            SaleInvoiceEntity(
                invoiceNumber = "INV-2026-0888",
                customerId = 3,
                customerName = "Maa Grocery & Confectionery",
                customerPhone = "+8801918776655",
                routeName = "Uttara Commercial Area",
                dateTimestamp = now - 3600000L * 30,
                dateFormatted = sdf.format(Date(now - 3600000L * 30)),
                itemsSummary = "Wheel Powder (5 Carton), Radhuni Mustard Oil (12 Bottle)",
                itemCount = 17,
                subtotal = 10740.0,
                discount = 240.0,
                grandTotal = 10500.0,
                paidAmount = 6000.0,
                dueAmount = 4500.0,
                paymentMode = "Cash",
                deliveryStatus = "PACKED",
                repName = "Mahmudul Hasan"
            ),
            SaleInvoiceEntity(
                invoiceNumber = "INV-2026-0887",
                customerId = 5,
                customerName = "New Dhaka Mini Mart",
                customerPhone = "+8801716554433",
                routeName = "Mirpur Sector 1-14 Route",
                dateTimestamp = now - 3600000L * 48,
                dateFormatted = sdf.format(Date(now - 3600000L * 48)),
                itemsSummary = "Pran Frooto (6 Box), Coca-Cola (8 Crate)",
                itemCount = 14,
                subtotal = 10560.0,
                discount = 260.0,
                grandTotal = 10300.0,
                paidAmount = 10300.0,
                dueAmount = 0.0,
                paymentMode = "Cash",
                deliveryStatus = "DELIVERED",
                repName = "Tanvir Ahmed"
            )
        )
        db.saleDao().insertAll(sampleSales)

        val samplePurchases = listOf(
            PurchaseInvoiceEntity(
                purchaseNumber = "PUR-2026-0310",
                supplierId = 1,
                supplierName = "Pran-RFL Group Central Depo",
                dateTimestamp = now - 3600000L * 50,
                dateFormatted = sdf.format(Date(now - 3600000L * 50)),
                itemsSummary = "Frooto Juice 250ml (100 Box), Bombay Crackers (100 Carton)",
                grandTotal = 114000.0,
                paidAmount = 70000.0,
                dueAmount = 44000.0
            ),
            PurchaseInvoiceEntity(
                purchaseNumber = "PUR-2026-0309",
                supplierId = 2,
                supplierName = "City Group (Teer Brands)",
                dateTimestamp = now - 3600000L * 90,
                dateFormatted = sdf.format(Date(now - 3600000L * 90)),
                itemsSummary = "Teer Soybean Oil 5L (60 Can), Mustard Oil (40 Bottle)",
                grandTotal = 59200.0,
                paidAmount = 50000.0,
                dueAmount = 9200.0
            )
        )
        db.purchaseDao().insertAll(samplePurchases)

        val sampleExpenses = listOf(
            ExpenseEntity(category = "Fuel & Transport", description = "Delivery Van diesel refill (Mirpur & Dhanmondi)", amount = 3800.0, dateFormatted = sdf.format(Date(now - 3600000L * 5)), dateTimestamp = now - 3600000L * 5),
            ExpenseEntity(category = "Rep Daily Allowance", description = "Lunch & conveyance for 4 sales reps", amount = 1600.0, dateFormatted = sdf.format(Date(now - 3600000L * 6)), dateTimestamp = now - 3600000L * 6),
            ExpenseEntity(category = "Warehouse Rent", description = "Depo monthly electricity & security bill", amount = 6500.0, dateFormatted = sdf.format(Date(now - 3600000L * 72)), dateTimestamp = now - 3600000L * 72),
            ExpenseEntity(category = "Vehicle Maintenance", description = "Delivery vehicle tyre puncture & oil change", amount = 1850.0, dateFormatted = sdf.format(Date(now - 3600000L * 120)), dateTimestamp = now - 3600000L * 120)
        )
        db.expenseDao().insertAll(sampleExpenses)

        val sampleReturns = listOf(
            ReturnRecordEntity(type = "SALES_RETURN", partyName = "Al-Madina General Store", reason = "Damaged packaging during transit (2 cans)", amount = 1700.0, dateFormatted = sdf.format(Date(now - 3600000L * 18)), dateTimestamp = now - 3600000L * 18),
            ReturnRecordEntity(type = "SUPPLIER_RETURN", partyName = "Pran-RFL Group Central Depo", reason = "Batch expiry return claim", amount = 3400.0, dateFormatted = sdf.format(Date(now - 3600000L * 40)), dateTimestamp = now - 3600000L * 40)
        )
        db.returnDao().insertAll(sampleReturns)
    }

    suspend fun recordSale(sale: SaleInvoiceEntity, items: List<CartItem>) {
        db.saleDao().insertSale(sale)
        for (item in items) {
            db.productDao().decreaseStock(item.productId, item.quantity)
        }
        if (sale.dueAmount > 0) {
            db.customerDao().adjustCustomerDue(sale.customerId, sale.dueAmount)
        }
    }

    suspend fun recordPurchase(purchase: PurchaseInvoiceEntity) {
        db.purchaseDao().insertPurchase(purchase)
        if (purchase.dueAmount > 0) {
            db.supplierDao().adjustSupplierDue(purchase.supplierId, purchase.dueAmount)
        }
    }

    suspend fun recordExpense(expense: ExpenseEntity) {
        db.expenseDao().insertExpense(expense)
    }

    suspend fun insertProduct(product: ProductEntity) {
        db.productDao().insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        db.productDao().updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        db.productDao().deleteProduct(product)
    }

    suspend fun insertCustomer(customer: CustomerEntity) {
        db.customerDao().insertCustomer(customer)
    }

    suspend fun insertSupplier(supplier: SupplierEntity) {
        db.supplierDao().insertSupplier(supplier)
    }

    suspend fun insertRoute(route: DistributionRouteEntity) {
        db.routeDao().insertRoute(route)
    }

    suspend fun advanceDeliveryStatus(invoiceId: Int, currentStatus: String) {
        val nextStatus = when (currentStatus) {
            "TAKEN" -> "PACKED"
            "PACKED" -> "IN_TRANSIT"
            "IN_TRANSIT" -> "DELIVERED"
            else -> "DELIVERED"
        }
        db.saleDao().updateDeliveryStatus(invoiceId, nextStatus)
    }

    suspend fun recordPayment(invoiceId: Int, customerId: Int, amount: Double) {
        db.saleDao().recordPayment(invoiceId, amount)
        db.customerDao().adjustCustomerDue(customerId, -amount)
    }
}
