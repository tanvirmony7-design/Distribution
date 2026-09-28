package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.DistroStrings
import com.example.ui.theme.DistroHeaderBlue
import com.example.ui.theme.DistroLightBlue
import com.example.ui.theme.DistroOrangeAction
import com.example.viewmodel.NavigationTab

data class NavItem(
    val tab: NavigationTab,
    val title: String,
    val icon: ImageVector,
    val testTag: String,
    val badgeCount: Int = 0
)

@Composable
fun DistroBottomNavBar(
    currentTab: NavigationTab,
    strings: DistroStrings,
    pendingDeliveriesCount: Int,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(
            tab = NavigationTab.DASHBOARD,
            title = strings.navDashboard,
            icon = Icons.Default.Dashboard,
            testTag = "nav_dashboard"
        ),
        NavItem(
            tab = NavigationTab.SALES,
            title = strings.navSales,
            icon = Icons.Default.PointOfSale,
            testTag = "nav_sales",
            badgeCount = pendingDeliveriesCount
        ),
        NavItem(
            tab = NavigationTab.PURCHASE,
            title = strings.navPurchase,
            icon = Icons.Default.ShoppingBag,
            testTag = "nav_purchase"
        ),
        NavItem(
            tab = NavigationTab.PRODUCTS,
            title = strings.navProducts,
            icon = Icons.Default.Inventory2,
            testTag = "nav_products"
        ),
        NavItem(
            tab = NavigationTab.REPORTS,
            title = strings.navReports,
            icon = Icons.Default.Assessment,
            testTag = "nav_reports"
        )
    )

    NavigationBar(
        modifier = modifier.testTag("distro_bottom_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    if (item.badgeCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = DistroOrangeAction,
                                    contentColor = Color.White
                                ) {
                                    Text(text = item.badgeCount.toString())
                                }
                            }
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DistroHeaderBlue,
                    selectedTextColor = DistroHeaderBlue,
                    indicatorColor = DistroLightBlue.copy(alpha = 0.2f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
