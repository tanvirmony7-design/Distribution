package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CustomerEntity
import com.example.data.ExpenseEntity
import com.example.data.SupplierEntity
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
import java.util.Locale

@Composable
fun CustomerSupplierScreen(
    viewModel: DistroViewModel,
    strings: DistroStrings,
    customers: List<CustomerEntity>,
    suppliers: List<SupplierEntity>,
    expenses: List<ExpenseEntity>,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val showAddCustomerDialog = viewModel.showAddCustomerDialog.value
    val showAddSupplierDialog = viewModel.showAddSupplierDialog.value
    val showAddExpenseDialog = viewModel.showAddExpenseDialog.value

    val formatCurrency = { amt: Double ->
        String.format(Locale.US, "৳ %,.2f", amt)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("customer_supplier_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = DistroHeaderBlue
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Customers (${customers.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Suppliers (${suppliers.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Expenses (${expenses.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        when (selectedTab) {
            0 -> {
                // Customers Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Retailer Stores & Dues Ledger",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DistroTextPrimary
                        )

                        Button(
                            onClick = { viewModel.showAddCustomerDialog.value = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DistroHeaderBlue),
                            modifier = Modifier.testTag("add_customer_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add Store", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(customers) { customer ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
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
                                        Column {
                                            Text(
                                                text = customer.shopName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = DistroTextPrimary
                                            )
                                            Text(
                                                text = "Prop: ${customer.ownerName} • ${customer.routeName}",
                                                fontSize = 11.sp,
                                                color = DistroTextSecondary
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.phone}"))
                                                context.startActivity(dial)
                                            },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(DistroGreenLight)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Call,
                                                contentDescription = "Call",
                                                tint = DistroGreenAction,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Credit Limit: ${formatCurrency(customer.creditLimit)}",
                                            fontSize = 11.sp,
                                            color = DistroTextSecondary
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (customer.totalDue > 0) DistroRedLight else DistroGreenLight
                                        ) {
                                            Text(
                                                text = if (customer.totalDue > 0) "Due: ${formatCurrency(customer.totalDue)}" else "Clear",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (customer.totalDue > 0) DistroRedAlert else DistroGreenAction,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Suppliers Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Suppliers & Manufacturer Depots",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DistroTextPrimary
                        )

                        Button(
                            onClick = { viewModel.showAddSupplierDialog.value = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DistroOrangeAction),
                            modifier = Modifier.testTag("add_supplier_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add Supplier", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(suppliers) { supplier ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
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
                                        Column {
                                            Text(
                                                text = supplier.companyName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = DistroTextPrimary
                                            )
                                            Text(
                                                text = "Contact: ${supplier.contactPerson} • ${supplier.address}",
                                                fontSize = 11.sp,
                                                color = DistroTextSecondary
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${supplier.phone}"))
                                                context.startActivity(dial)
                                            },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(DistroOrangeLight)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Call,
                                                contentDescription = "Call",
                                                tint = DistroOrangeAction,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Phone: ${supplier.phone}",
                                            fontSize = 11.sp,
                                            color = DistroTextSecondary
                                        )

                                        Text(
                                            text = "Payable Due: ${formatCurrency(supplier.totalDue)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = DistroOrangeAction
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Expenses Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Distribution Expenses",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DistroTextPrimary
                        )

                        Button(
                            onClick = { viewModel.showAddExpenseDialog.value = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DistroRedAlert),
                            modifier = Modifier.testTag("add_expense_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add Expense", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(expenses) { exp ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
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
                                    Column {
                                        Text(
                                            text = exp.category,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = DistroTextPrimary
                                        )
                                        Text(
                                            text = "${exp.description} • ${exp.dateFormatted}",
                                            fontSize = 11.sp,
                                            color = DistroTextSecondary
                                        )
                                    }

                                    Text(
                                        text = formatCurrency(exp.amount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = DistroRedAlert
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Customer Dialog
    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { viewModel.showAddCustomerDialog.value = false },
            onSave = { shopName, owner, phone, address, route, credit ->
                viewModel.addNewCustomer(shopName, owner, phone, address, route, credit)
            }
        )
    }

    // Add Supplier Dialog
    if (showAddSupplierDialog) {
        AddSupplierDialog(
            onDismiss = { viewModel.showAddSupplierDialog.value = false },
            onSave = { name, person, phone, addr ->
                viewModel.insertSupplier(com.example.data.SupplierEntity(companyName = name, contactPerson = person, phone = phone, address = addr))
            }
        )
    }

    // Add Expense Dialog
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { viewModel.showAddExpenseDialog.value = false },
            onSave = { cat, desc, amt ->
                viewModel.addNewExpense(cat, desc, amt)
            }
        )
    }
}

@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, Double) -> Unit
) {
    var shopName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+8801") }
    var address by remember { mutableStateOf("") }
    var routeName by remember { mutableStateOf("Mirpur Sector 1-14 Route") }
    var creditLimitText by remember { mutableStateOf("50000") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Register Retail Customer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = shopName, onValueChange = { shopName = it }, label = { Text("Shop / Store Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = ownerName, onValueChange = { ownerName = it }, label = { Text("Owner Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Store Address") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (shopName.isNotBlank()) {
                            onSave(shopName, ownerName, phone, address, routeName, creditLimitText.toDoubleOrNull() ?: 50000.0)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DistroHeaderBlue)
                ) {
                    Text("Save Customer")
                }
            }
        }
    }
}

@Composable
fun AddSupplierDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var companyName by remember { mutableStateOf("") }
    var contactPerson by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+8801") }
    var address by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add Supplier / Depot", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = companyName, onValueChange = { companyName = it }, label = { Text("Company Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = contactPerson, onValueChange = { contactPerson = it }, label = { Text("Contact Person") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (companyName.isNotBlank()) {
                            onSave(companyName, contactPerson, phone, address)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DistroOrangeAction)
                ) {
                    Text("Save Supplier")
                }
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Double) -> Unit
) {
    var category by remember { mutableStateOf("Fuel & Transport") }
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Record Distribution Expense", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Fuel, Rep Daily, Rent)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Expense Details") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = amountText, onValueChange = { amountText = it }, label = { Text("Amount (৳)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            onSave(category, description, amt)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DistroRedAlert)
                ) {
                    Text("Record Expense")
                }
            }
        }
    }
}
