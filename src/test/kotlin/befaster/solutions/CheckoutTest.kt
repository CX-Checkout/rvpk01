package befaster.solutions

import com.memoizr.assertk.expect
import org.junit.Test


class CheckoutTest {
    @Test
    fun `item for a price`(){
        expect that A.price isEqualTo 50
        expect that B.price isEqualTo 30
        expect that C.price isEqualTo 20
        expect that D.price isEqualTo 15
        expect that E.price isEqualTo 40
    }


    @Test
    fun `checkout should sum the price of the item in the basket`(){
        val basket: Basket = listOf(A, A, B, C, D)
        val checkout = Checkout(basket)

        expect that checkout.total isEqualTo A.price + A.price + B.price + C.price + D.price
    }

    @Test
    fun `checkout should discount items`(){
        expect that Checkout(listOf(A)).total isEqualTo 50
        expect that Checkout(listOf(A, A, A)).total isEqualTo 130
        expect that Checkout(listOf(A, A, A, A)).total isEqualTo 130 + A.price
        expect that Checkout(listOf(A, A, A, B)).total isEqualTo 130 + B.price
        expect that Checkout(listOf(B)).total isEqualTo 30
        expect that Checkout(listOf(B, B)).total isEqualTo 45
        expect that Checkout(listOf(B, B, B)).total isEqualTo 45 + B.price
        expect that Checkout(listOf(C)).total isEqualTo C.price
    }

    @Test
    fun `it should parse a valid list of items and return total`(){

        expect that Checkout.priceFor("B") isEqualTo B.price
        expect that Checkout.priceFor("A") isEqualTo A.price
        expect that Checkout.priceFor("B") isEqualTo B.price
        expect that Checkout.priceFor("C") isEqualTo C.price
        expect that Checkout.priceFor("D") isEqualTo D.price
        expect that Checkout.priceFor("E") isEqualTo E.price
        expect that Checkout.priceFor("ABCDABC") isEqualTo 200
    }

    @Test
    fun `it should return invalid value for invalid input`(){
        val items = "ABCDABFOO"

        expect that Checkout.priceFor(items) isEqualTo -1
    }

    @Test
    fun `it should implement multiple offers for same item`(){

        expect that Checkout(listOf(A, A, A)).total isEqualTo 130
        expect that Checkout(listOf(A, A, A, A)).total isEqualTo 130 + A.price
        expect that Checkout(listOf(A, A, A, A, A)).total isEqualTo 200
        expect that Checkout(listOf(A, A, A, A, A, A)).total isEqualTo Math.min(200 + A.price, (130 * 2) + A.price)

        expect that Checkout.priceFor("AAAAAAAA") isEqualTo 330
        expect that Checkout.priceFor("AAAAAEEBAAABB") isEqualTo 455
        expect that Checkout.priceFor("AAAAAAAAA") isEqualTo 390
    }

    @Test
    fun `it should implement buy one item to get other items for free`(){

        expect that Checkout(listOf(E, E, B)).total isEqualTo 2 * E.price
        expect that Checkout(listOf(E, E, C)).total isEqualTo 2 * E.price + C.price
        expect that Checkout(listOf(E, E, B, B)).total isEqualTo 2 * E.price + B.price
        expect that Checkout(listOf(E, E, B, B)).total isEqualTo Math.min(2 * E.price + B.price, 2 * E.price + B.price)

    }

    @Test
    fun buyItemGetItemFreeTest(){

    }

}