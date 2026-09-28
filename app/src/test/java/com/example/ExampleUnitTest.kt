package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCartItemCalculation() {
    val cartItem = com.example.data.CartItem(
        productId = 1,
        productName = "Pran Frooto",
        unit = "Box",
        unitPrice = 480.0,
        quantity = 5
    )
    assertEquals(2400.0, cartItem.total, 0.001)
  }

  @Test
  fun testBilingualStringsIntegrity() {
    val en = com.example.ui.localization.EnglishStrings
    val bn = com.example.ui.localization.BengaliStrings
    assertNotNull(en.totalSales)
    assertNotNull(bn.totalSales)
    assertEquals("Total Sales", en.totalSales)
    assertEquals("মোট বিক্রি", bn.totalSales)
    assertEquals("গ্রাহক বকেয়া", bn.customerDue)
    assertEquals("Customer Due", en.customerDue)
  }
}
