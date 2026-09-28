package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
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
import com.example.data.DistributionRouteEntity
import com.example.data.SaleInvoiceEntity
import com.example.ui.localization.DistroStrings
import com.example.ui.theme.DistroAccentCyan
import com.example.ui.theme.DistroGreenAction
import com.example.ui.theme.DistroGreenLight
import com.example.ui.theme.DistroHeaderBlue
import com.example.ui.theme.DistroLightBlue
import com.example.ui.theme.DistroOrangeAction
import com.example.ui.theme.DistroOrangeLight
import com.example.ui.theme.DistroRedAlert
import com.example.ui.theme.DistroTextPrimary
import com.example.ui.theme.DistroTextSecondary
import com.example.viewmodel.DistroViewModel

@Composable
fun RoutesDeliveryScreen(
    viewModel: DistroViewModel,
    strings: DistroStrings,
    routes: List<DistributionRouteEntity>,
    sales: List<SaleInvoiceEntity>,
    onInvoiceClick: (SaleInvoiceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var deliveryFilterStage by remember { mutableStateOf("ALL") }
    val showAddRouteDialog = viewModel.showAddRouteDialog.value

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("routes_delivery_screen")
    ) {
        // Tab Row: 1. Delivery Routes & Reps, 2. Live Delivery Tracker Pipeline
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = DistroHeaderBlue
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Routes & Reps (${routes.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Delivery Pipeline", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
        }

        if (selectedTab == 0) {
            // Tab 1: Route & Reps Management
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
                    Column {
                        Text(
                            text = strings.routeAreaManagement,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DistroTextPrimary
                        )
                        Text(
                            text = "Assigned Delivery Zones & Vehicle Reps",
                            fontSize = 11.sp,
                            color = DistroTextSecondary
                        )
                    }

                    Button(
                        onClick = { viewModel.showAddRouteDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DistroHeaderBlue),
                        modifier = Modifier.testTag("add_route_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = strings.addRouteBtn, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(routes) { route ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("route_card_${route.id}"),
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(DistroLightBlue.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AltRoute,
                                                contentDescription = "Route",
                                                tint = DistroHeaderBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = route.routeName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DistroHeaderBlue
                                            )
                                            Text(
                                                text = "Vehicle: ${route.vehicleNo}",
                                                fontSize = 11.sp,
                                                color = DistroTextSecondary
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = DistroGreenLight
                                    ) {
                                        Text(
                                            text = strings.activeRepStatus,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DistroGreenAction,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Person, contentDescription = "Rep", tint = DistroTextSecondary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "Rep: ${route.assignedRepName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Store, contentDescription = "Store", tint = DistroTextSecondary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "${route.assignedShopsCount} Retail Stores Assigned", fontSize = 11.sp, color = DistroTextSecondary)
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${route.repPhone}")
                                            }
                                            context.startActivity(dialIntent)
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
                            }
                        }
                    }
                }
            }
        } else {
            // Tab 2: Live Delivery Tracking Pipeline: Order Taken -> Packed -> In Transit -> Delivered
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Order Delivery Pipeline",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DistroTextPrimary
                )
                Text(
                    text = "Track progression: Taken ➔ Packed ➔ In Transit ➔ Delivered",
                    fontSize = 11.sp,
                    color = DistroTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Stage filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL", "TAKEN", "PACKED", "IN_TRANSIT", "DELIVERED").forEach { stage ->
                        FilterChip(
                            selected = deliveryFilterStage == stage,
                            onClick = { deliveryFilterStage = stage },
                            label = {
                                Text(
                                    text = when (stage) {
                                        "ALL" -> "All"
                                        "TAKEN" -> "1. Taken"
                                        "PACKED" -> "2. Packed"
                                        "IN_TRANSIT" -> "3. Transit"
                                        "DELIVERED" -> "4. Done"
                                        else -> stage
                                    },
                                    fontSize = 10.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DistroHeaderBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val trackedSales = sales.filter {
                    if (deliveryFilterStage == "ALL") true else it.deliveryStatus == deliveryFilterStage
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (trackedSales.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No orders at this delivery stage.", color = DistroTextSecondary)
                            }
                        }
                    }

                    items(trackedSales) { invoice ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onInvoiceClick(invoice) },
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
                                    Column {
                                        Text(
                                            text = invoice.invoiceNumber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = DistroHeaderBlue
                                        )
                                        Text(
                                            text = invoice.customerName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = DistroTextPrimary
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (invoice.deliveryStatus) {
                                            "DELIVERED" -> DistroGreenLight
                                            "IN_TRANSIT" -> DistroOrangeLight
                                            else -> DistroLightBlue.copy(alpha = 0.15f)
                                        }
                                    ) {
                                        Text(
                                            text = invoice.deliveryStatus,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (invoice.deliveryStatus) {
                                                "DELIVERED" -> DistroGreenAction
                                                "IN_TRANSIT" -> DistroOrangeAction
                                                else -> DistroHeaderBlue
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Route: ${invoice.routeName} • Rep: ${invoice.repName}",
                                    fontSize = 11.sp,
                                    color = DistroTextSecondary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Bill: ৳ ${invoice.grandTotal}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DistroTextPrimary
                                    )

                                    if (invoice.deliveryStatus != "DELIVERED") {
                                        Button(
                                            onClick = { viewModel.advanceDeliveryStage(invoice) },
                                            colors = ButtonDefaults.buttonColors(containerColor = DistroHeaderBlue),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(
                                                text = when (invoice.deliveryStatus) {
                                                    "TAKEN" -> "Pack Order ➔"
                                                    "PACKED" -> "Ship Transit ➔"
                                                    "IN_TRANSIT" -> "Mark Delivered ✔"
                                                    else -> "Update"
                                                },
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Route Dialog
    if (showAddRouteDialog) {
        AddRouteDialog(
            viewModel = viewModel,
            strings = strings,
            onDismiss = { viewModel.showAddRouteDialog.value = false }
        )
    }
}

@Composable
fun AddRouteDialog(
    viewModel: DistroViewModel,
    strings: DistroStrings,
    onDismiss: () -> Unit
) {
    var routeName by remember { mutableStateOf("") }
    var repName by remember { mutableStateOf("") }
    var repPhone by remember { mutableStateOf("+8801") }
    var vehicleNo by remember { mutableStateOf("Dhaka-Metro-Ta-") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
                .testTag("add_route_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.addRouteBtn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DistroTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = routeName,
                    onValueChange = { routeName = it },
                    label = { Text("Route / Area Name (e.g. Uttara Commercial)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = repName,
                    onValueChange = { repName = it },
                    label = { Text("Assigned Sales Rep Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = repPhone,
                    onValueChange = { repPhone = it },
                    label = { Text("Rep Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = vehicleNo,
                    onValueChange = { vehicleNo = it },
                    label = { Text("Delivery Vehicle No.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (routeName.isNotBlank() && repName.isNotBlank()) {
                            viewModel.addNewRoute(routeName, repName, repPhone, vehicleNo)
                        } else {
                            viewModel.showToast("Please provide Route Name and Sales Rep Name.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DistroHeaderBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Route", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
