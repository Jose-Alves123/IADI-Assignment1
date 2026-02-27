package pt.unl.fct.iadi.orderprocessingplatform.payment

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.orderprocessingplatform.domain.PaymentRequest
import pt.unl.fct.iadi.orderprocessingplatform.domain.Receipt
import pt.unl.fct.iadi.orderprocessingplatform.domain.ReceiptStatus

@Component
@Profile("!prod")
class SandboxPaymentGateway : PaymentGateway {
    override fun processPayment(paymentRequest: PaymentRequest): Receipt {
        return Receipt(
            paymentRequest.orderId,
            ReceiptStatus.PAID,
            mapOf(
                "gateway" to "sandbox",
                "amount" to paymentRequest.amount,
            )
        )
    }
}