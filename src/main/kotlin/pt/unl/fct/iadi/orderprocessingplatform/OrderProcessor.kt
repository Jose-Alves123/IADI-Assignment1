package pt.unl.fct.iadi.orderprocessingplatform

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.orderprocessingplatform.domain.Order
import pt.unl.fct.iadi.orderprocessingplatform.domain.PaymentRequest
import pt.unl.fct.iadi.orderprocessingplatform.payment.PaymentGateway
import pt.unl.fct.iadi.orderprocessingplatform.pricing.PriceCalculator
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

@Component
class OrderProcessor(
    private val calculator: PriceCalculator,
    private val payment: PaymentGateway
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val sampleOrder = Order(
            id = "ORD-2026-001",
            userId = "user123",
            items = listOf(
                Order.OrderItem("LAPTOP-001", 2, 999.99),
                Order.OrderItem("MOUSE-042", 3, 29.99),
                Order.OrderItem("KEYBOARD-123", 6, 149.99)
            )
        )

        processOrder(sampleOrder).forEach(::println)
    }

    fun processOrder(order: Order): List<String> {
        val totalPrice = calculator.calculateTotalPrice(order)

        val roundedPrice = BigDecimal(totalPrice)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

        val paymentRequest = PaymentRequest(
            orderId = order.id,
            amount = roundedPrice,
        )
        val receipt = payment.processPayment(paymentRequest)

        val lines = mutableListOf(
            "Order ID: ${order.id}",
            "User ID: ${order.userId}",
            "Created at: ${order.createdAt}",
            "",
            "Items:"
        )

        order.items.forEach { item ->
            val itemTotal = BigDecimal(item.price * item.quantity)
                .setScale(2, RoundingMode.HALF_UP)
                .toDouble()

            lines += "  - ${item.productId}: ${item.quantity} x $${"%.2f".format(Locale.US, item.price)} = $${"%.2f".format(Locale.US, itemTotal)}"
        }

        lines += ""
        lines += "Total Price: $${"%.2f".format(Locale.US, roundedPrice)}"
        lines += "Calculator Used: ${calculator::class.simpleName}"
        lines += ""
        lines += "Payment Status: ${receipt.status}"
        lines += "Payment Gateway: ${receipt.metadata["gateway"]}"

        receipt.metadata["transactionId"]?.let {
            lines += "Transaction ID: $it"
        }

        receipt.metadata["reason"]?.let {
            lines += "Reason: $it"
        }

        lines += ""
        lines += "=== Processing Complete ==="

        return lines
    }
}