package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQty = stockQty - :qty WHERE id = :productId")
    suspend fun decreaseStock(productId: Int, qty: Int)

    @Query("UPDATE products SET stockQty = stockQty + :qty WHERE id = :productId")
    suspend fun increaseStock(productId: Int, qty: Int)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY shopName ASC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerEntity>)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Query("UPDATE customers SET totalDue = totalDue + :dueChange WHERE id = :customerId")
    suspend fun adjustCustomerDue(customerId: Int, dueChange: Double)
}

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers ORDER BY companyName ASC")
    fun getAllSuppliers(): Flow<List<SupplierEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: SupplierEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(suppliers: List<SupplierEntity>)

    @Update
    suspend fun updateSupplier(supplier: SupplierEntity)

    @Query("UPDATE suppliers SET totalDue = totalDue + :dueChange WHERE id = :supplierId")
    suspend fun adjustSupplierDue(supplierId: Int, dueChange: Double)
}

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales_invoices ORDER BY dateTimestamp DESC")
    fun getAllSales(): Flow<List<SaleInvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleInvoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sales: List<SaleInvoiceEntity>)

    @Query("UPDATE sales_invoices SET deliveryStatus = :status WHERE id = :id")
    suspend fun updateDeliveryStatus(id: Int, status: String)

    @Query("UPDATE sales_invoices SET paidAmount = paidAmount + :payment, dueAmount = dueAmount - :payment WHERE id = :id")
    suspend fun recordPayment(id: Int, payment: Double)
}

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchase_invoices ORDER BY dateTimestamp DESC")
    fun getAllPurchases(): Flow<List<PurchaseInvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseInvoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(purchases: List<PurchaseInvoiceEntity>)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY dateTimestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<ExpenseEntity>)
}

@Dao
interface RouteDao {
    @Query("SELECT * FROM distribution_routes ORDER BY routeName ASC")
    fun getAllRoutes(): Flow<List<DistributionRouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: DistributionRouteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routes: List<DistributionRouteEntity>)

    @Update
    suspend fun updateRoute(route: DistributionRouteEntity)
}

@Dao
interface ReturnDao {
    @Query("SELECT * FROM return_records ORDER BY dateTimestamp DESC")
    fun getAllReturns(): Flow<List<ReturnRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturn(returnRecord: ReturnRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(returns: List<ReturnRecordEntity>)
}
