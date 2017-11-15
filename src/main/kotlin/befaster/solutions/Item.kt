package befaster.solutions

sealed abstract class Item(open val price: Price)

object A : Item(50)
object B : Item(30)
object C : Item(20)
object D : Item(15)
object Invalid : Item(-1)

typealias Basket = List<Item>
typealias Price = Int

class Checkout(basket: Basket) {
    val total: Price = if(basket.contains(Invalid)) -1 else
            basket.groupBy { it }.map {
                when {
                    it.key is A -> bulkPrice(it.key, 3, it.value.size, 130)
                    it.key is B -> bulkPrice(it.key, 2, it.value.size, 45)
                    else -> it.value.sumBy { it.price }
                }
            }.sum()

    private fun bulkPrice(item: Item, itemsOnOffer: Int, numberOfItems: Int, pricePerBulkItems: Int): Int {
        return if (numberOfItems % itemsOnOffer == 0) reducedPrice(numberOfItems, pricePerBulkItems, itemsOnOffer)
        else {
            reducedPrice(numberOfItems, pricePerBulkItems, itemsOnOffer) + (numberOfItems.mod(itemsOnOffer)) * item.price
        }
    }

    private fun reducedPrice(numberOfItems: Int, bulkPrice: Int, itemsOnOffer: Int) = bulkPrice * (numberOfItems / itemsOnOffer)

    companion object {
        fun priceFor(items: String): Price = Checkout(Scanner().getBasketFor(items)).total
    }
}

class Scanner {
    fun getBasketFor(itemsScanned: String): Basket = itemsScanned.map {
        when (it) {
            'A' -> A
            'B' -> B
            'C' -> C
            'D' -> D
            else -> Invalid
        }
    }

}