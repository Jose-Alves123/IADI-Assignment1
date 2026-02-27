package pt.unl.fct.iadi.orderprocessingplatform.pricing

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.orderprocessingplatform.domain.Order

@Component
@ConditionalOnProperty(prefix = "pricing.promo", name = ["enabled"], havingValue = "false", matchIfMissing = true)
class BasicPriceCalculator : PriceCalculator {

    override fun calculateTotalPrice(order: Order): Double {
        var res = 0.0

        for (orderItem in order.items) {
            res += orderItem.price * orderItem.quantity
        }

        return res
    }
}