package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SaleInvoiceEntity
import com.example.ui.localization.DistroStrings
import com.example.ui.theme.DistroGreenAction
import com.example.ui.theme.DistroGreenLight
import com.example.ui.theme.DistroHeaderBlue
import com.example.ui.theme.DistroOrangeAction
import com.example.ui.theme.DistroOrangeLight
import com.example.ui.theme.DistroRedAlert
import com.example.ui.theme.DistroRedLight

@Composable
fun ThermalReceiptDialog(
    invoice: SaleInvoiceEntity,
    strings: DistroStrings,
    onDismiss: () -> Unit,
    onAdvanceDeliveryStatus: (SaleInvoiceEntity) -> Unit,
    onRecordPayment: (SaleInvoiceEntity, Double) -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    var showPaymentInput by remember { mutableStateOf(false) }
    var paymentInputText by remember { mutableStateOf("") }

    val formattedCurrency = { amount: Double ->
        String.format(java.util.Locale.US, "৳ %,.2f", amount)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .testTag("thermal_receipt_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header with close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Printer",
                            tint = DistroHeaderBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.thermalReceiptTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DistroHeaderBlue
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_receipt_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Delivery Status Pipeline
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DistroHeaderBlue.copy(alpha = 0.08f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = "Delivery",
                                tint = DistroHeaderBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = strings.deliveryStatus,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = when (invoice.deliveryStatus) {
                                        "TAKEN" -> strings.stageTaken
                                        "PACKED" -> strings.stagePacked
                                        "IN_TRANSIT" -> strings.stageInTransit
                                        "DELIVERED" -> strings.stageDelivered
                                        else -> invoice.deliveryStatus
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (invoice.deliveryStatus == "DELIVERED") DistroGreenAction else DistroOrangeAction
                                )
                            }
                        }

                        if (invoice.deliveryStatus != "DELIVERED") {
                            Button(
                                onClick = { onAdvanceDeliveryStatus(invoice) },
                                colors = ButtonDefaults.buttonColors(containerColor = DistroHeaderBlue),
                                modifier = Modifier
                                    .testTag("advance_delivery_stage_button")
                                    .height(34.dp)
                            ) {
                                Text(text = strings.markNextDeliveryStage, fontSize = 11.sp)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Delivered",
                                    tint = DistroGreenAction,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.stageDelivered,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DistroGreenAction
                                )
                            }
                        }
                    }
                }

                // Thermal Paper Container (Styled like 58mm/80mm roll receipt)
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFAFAF7))
                        .border(1.dp, Color(0xFFD6D3D1), RoundedCornerShape(8.dp))
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "DISTROMASTER DMS",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp,
                            color = Color(0xFF1C1917)
                        )
                        Text(
                            text = "WHOLESALE & DISTRIBUTION DEPOT",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF44403C)
                        )
                        Text(
                            text = "Mirpur Road, Dhaka | HotLine: 01711223344",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF78716C)
                        )
                        Text(
                            text = "BIN: 0024981729 | Route Rep: ${invoice.repName}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF78716C)
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "------------------------------------------",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFFA8A29E)
                        )

                        // Invoice & Customer Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Inv: ${invoice.invoiceNumber}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1C1917)
                            )
                            Text(
                                text = invoice.dateFormatted,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF57534E)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Store: ${invoice.customerName}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1C1917),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = invoice.customerPhone,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF57534E)
                            )
                        }

                        Text(
                            text = "Route: ${invoice.routeName}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF78716C),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "==========================================",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFFA8A29E)
                        )

                        // Line items summary
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "ITEMS ORDERED:",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF44403C)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = invoice.itemsSummary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF1C1917)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "------------------------------------------",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFFA8A29E)
                        )

                        // Calculation Breakdown
                        ThermalRow(label = strings.subtotal, value = formattedCurrency(invoice.subtotal))
                        if (invoice.discount > 0) {
                            ThermalRow(label = strings.discount, value = "- " + formattedCurrency(invoice.discount))
                        }
                        ThermalRow(label = strings.taxVat, value = "৳ 0.00")

                        Text(
                            text = "==========================================",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFFA8A29E)
                        )

                        ThermalRow(
                            label = strings.grandTotal,
                            value = formattedCurrency(invoice.grandTotal),
                            isBold = true,
                            fontSize = 13
                        )
                        ThermalRow(
                            label = strings.paidAmount + " (${invoice.paymentMode})",
                            value = formattedCurrency(invoice.paidAmount),
                            color = Color(0xFF15803D)
                        )

                        if (invoice.dueAmount > 0) {
                            ThermalRow(
                                label = strings.dueAmount,
                                value = formattedCurrency(invoice.dueAmount),
                                isBold = true,
                                color = Color(0xFFB91C1C)
                            )
                        } else {
                            ThermalRow(
                                label = "BALANCE",
                                value = "PAID IN FULL",
                                color = Color(0xFF15803D),
                                isBold = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Stylized barcode rendering
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val pattern = listOf(2, 1, 3, 1, 2, 4, 1, 2, 3, 2, 1, 3, 2, 4, 1, 2, 3, 1, 2, 1, 3, 2, 1, 4, 2)
                            pattern.forEach { w ->
                                Box(
                                    modifier = Modifier
                                        .width((w * 1.5).dp)
                                        .height(26.dp)
                                        .background(Color(0xFF1C1917))
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }

                        Text(
                            text = "* ${invoice.invoiceNumber} *",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            letterSpacing = 2.sp,
                            color = Color(0xFF57534E)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = strings.thankyouNote,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF78716C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Recording Section if there is due
                if (invoice.dueAmount > 0) {
                    if (!showPaymentInput) {
                        OutlinedButton(
                            onClick = { showPaymentInput = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("record_due_payment_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DistroGreenAction)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = "Payment")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Collect Payment against Due (${formattedCurrency(invoice.dueAmount)})")
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = paymentInputText,
                                onValueChange = { paymentInputText = it },
                                label = { Text("Collect Amount (৳)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("collect_payment_input"),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    val amt = paymentInputText.toDoubleOrNull() ?: 0.0
                                    if (amt > 0) {
                                        onRecordPayment(invoice, amt.coerceAtMost(invoice.dueAmount))
                                        showPaymentInput = false
                                        paymentInputText = ""
                                    } else {
                                        onShowToast("Enter a valid amount")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DistroGreenAction),
                                modifier = Modifier.testTag("confirm_payment_button")
                            ) {
                                Text("Save")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Action Buttons: Print Thermal BT & Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onShowToast("${strings.printerConnected}: ${strings.printingSimulation}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DistroHeaderBlue),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("print_bluetooth_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bluetooth,
                            contentDescription = "Print BT",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.printBluetooth,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            shareReceiptText(context, invoice)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_invoice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.shareInvoice,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThermalRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    fontSize: Int = 11,
    color: Color = Color(0xFF1C1917)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = fontSize.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = fontSize.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}

private fun shareReceiptText(context: Context, invoice: SaleInvoiceEntity) {
    val text = buildString {
        appendLine("--- DISTROMASTER DISTRIBUTION BILL ---")
        appendLine("Invoice No: ${invoice.invoiceNumber}")
        appendLine("Date: ${invoice.dateFormatted}")
        appendLine("Customer Store: ${invoice.customerName}")
        appendLine("Route: ${invoice.routeName} | Rep: ${invoice.repName}")
        appendLine("Items: ${invoice.itemsSummary}")
        appendLine("Total Bill: ৳ ${invoice.grandTotal}")
        appendLine("Paid Amount: ৳ ${invoice.paidAmount} (${invoice.paymentMode})")
        appendLine("Due Amount: ৳ ${invoice.dueAmount}")
        appendLine("Delivery Status: ${invoice.deliveryStatus}")
        appendLine("Thank you for your business!")
    }
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Invoice Bill"))
}
