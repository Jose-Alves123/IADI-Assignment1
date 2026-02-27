package pt.unl.fct.iadi.orderprocessingplatform.pricing

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.orderprocessingplatform.domain.Order

@Component
@ConditionalOnProperty(prefix = "pricing.promo", name = ["enabled"], havingValue = "true")
class PromoPriceCalculator: PriceCalculator {

    private val discount : Double = 0.8
    private val threshold : Int = 5

    override fun calculateTotalPrice(order: Order): Double {
        var res = 0.0

        for (item in order.items) {
            res += if (item.quantity > threshold) {
                item.price * item.quantity * discount
            } else {
                item.price * item.quantity
            }
        }

        return res
    }
}