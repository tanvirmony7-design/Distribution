package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val sku: String,
    val barcode: String,
    val unit: String,
    val buyPrice: Double,
    val sellPrice: Double,
    val stockQty: Int,
    val minStockAlert: Int = 20
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val shopName: String,
    val ownerName: String,
    val phone: String,
    val address: String,
    val routeName: String,
    val totalDue: Double = 0.0,
    val creditLimit: Double = 50000.0
)

@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyName: String,
    val contactPerson: String,
    val phone: String,
    val address: String,
    val totalDue: Double = 0.0
)

@Entity(tableName = "sales_invoices")
data class SaleInvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceNumber: String,
    val customerId: Int,
    val customerName: String,
    val customerPhone: String,
    val routeName: String,
    val dateTimestamp: Long,
    val dateFormatted: String,
    val itemsSummary: String,
    val itemCount: Int,
    val subtotal: Double,
    val discount: Double,
    val grandTotal: Double,
    val paidAmount: Double,
    val dueAmount: Double,
    val paymentMode: String,
    val deliveryStatus: String, // "TAKEN", "PACKED", "IN_TRANSIT", "DELIVERED"
    val repName: String
)

@Entity(tableName = "purchase_invoices")
data class PurchaseInvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val purchaseNumber: String,
    val supplierId: Int,
    val supplierName: String,
    val dateTimestamp: Long,
    val dateFormatted: String,
    val itemsSummary: String,
    val grandTotal: Double,
    val paidAmount: Double,
    val dueAmount: Double
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val description: String,
    val amount: Double,
    val dateFormatted: String,
    val dateTimestamp: Long
)

@Entity(tableName = "distribution_routes")
data class DistributionRouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routeName: String,
    val assignedRepName: String,
    val repPhone: String,
    val assignedShopsCount: Int,
    val pendingDeliveriesCount: Int,
    val vehicleNo: String
)

@Entity(tableName = "return_records")
data class ReturnRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "SALES_RETURN" or "SUPPLIER_RETURN"
    val partyName: String,
    val reason: String,
    val amount: Double,
    val dateFormatted: String,
    val dateTimestamp: Long
)

// Helper model for invoice line items
data class CartItem(
    val productId: Int,
    val productName: String,
    val unit: String,
    val unitPrice: Double,
    var quantity: Int
) {
    val total: Double get() = unitPrice * quantity
}
