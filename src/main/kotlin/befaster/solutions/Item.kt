package befaster.solutions

sealed abstract class Item(open val price: Price)

object A : Item(50)
object B : Item(30)
object C : Item(20)
object D : Item(15)
object E : Item(40)
object Invalid : Item(-1)

typealias Basket = List<Item>
typealias Price = Int

interface Deal {
    val howManyYouNeedToBuy: Int
}
data class NAtPriceOf(val item: Item, override val howManyYouNeedToBuy: Int, val priceOf: Price): Deal {
    fun reducedPrice(numberOfItems: Int): Price {
        return (numberOfItems % howManyYouNeedToBuy) * item.price + (numberOfItems / howManyYouNeedToBuy) * priceOf
    }
}

data class BuyItemGetItemFree(
        val whatYouGetForFree: Item,
        val whatYouNeedToBuy: Item,
        override val howManyYouNeedToBuy: Int,
        val howManyYouGetForFree: Int) : Deal {

    fun reducedPrice(howManyYouHave: Int, howManyOfOther: Int): Price {
        val numberOfDiscounts = howManyOfOther / howManyYouNeedToBuy
        val result = ((howManyYouHave - numberOfDiscounts) * whatYouGetForFree.price)
        return Math.max(0, result)
    }

}
object NoDeal: Deal {
    override val howManyYouNeedToBuy: Int = 1
}

val offers = mapOf(
        A to listOf(NAtPriceOf(A, 3,130), NAtPriceOf(A, 5, 200)),
        B to listOf(BuyItemGetItemFree(B, E, 2, 1), NAtPriceOf(B, 2, 45))
)

class Checkout(basket: Basket) {
    companion object {
        fun priceFor(items: String): Price = Checkout(Scanner().getBasketFor(items)).total
    }

    val total: Price = if(basket.contains(Invalid)) -1 else {
        val groupedItems = basket.groupBy { it }
        val priceForItems = groupedItems.map { group ->
            val deals = offers[group.key] ?: listOf(NoDeal)
            val reducedPrice = getPriceFor(group.key, group.value.size, deals, groupedItems)
            reducedPrice.min()!!
        }.sum()
        priceForItems
    }

    fun getPriceFor(item: Item, numberOfItems: Int, deals: List<Deal>, group: Map<Item, List<Item>>): List<Int> {
        fun go(list: List<Item>): List<Int> {
            return when {
                list.isEmpty() -> listOf(0)
                else -> (listOf(numberOfItems) + deals.map { it.howManyYouNeedToBuy }).flatMap { howMany ->
                    deals.map {
                        when (it) {
                            is NAtPriceOf -> it.reducedPrice(howMany) + go(list.drop(howMany)).min()!!
                            is BuyItemGetItemFree -> it.reducedPrice(list.size, group[it.whatYouNeedToBuy].orEmpty().size) + go(list.drop(it.howManyYouNeedToBuy)).min()!!
                            else -> item.price * howMany + go(list.drop(howMany)).min()!!
                        }
                    }
                }
            }
        }
        return go(group[item].orEmpty())
    }

}

class Scanner {
    fun getBasketFor(itemsScanned: String): Basket = itemsScanned.map {
        when (it) {
            'A' -> A
            'B' -> B
            'C' -> C
            'D' -> D
            'E' -> E
            else -> Invalid
        }
    }

}