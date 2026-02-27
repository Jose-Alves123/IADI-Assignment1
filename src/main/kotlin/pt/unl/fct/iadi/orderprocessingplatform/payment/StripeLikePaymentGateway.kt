package pt.unl.fct.iadi.orderprocessingplatform.payment

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.orderprocessingplatform.domain.PaymentRequest
import pt.unl.fct.iadi.orderprocessingplatform.domain.Receipt
import pt.unl.fct.iadi.orderprocessingplatform.domain.ReceiptStatus
import java.util.UUID

@Component
@Profile("prod")
class StripeLikePaymentGateway: PaymentGateway {
    override fun processPayment(paymentRequest: PaymentRequest): Receipt {
        val transactionId = UUID.randomUUID().toString()

        if(paymentRequest.amount <= 0){
            return Receipt(
                orderId = paymentRequest.orderId,
                ReceiptStatus.REJECTED,
                mapOf(
                    "gateway" to "stripe-like",
                    "reason" to "Invalid amount",
                    "amount" to paymentRequest.amount
                )
            )
        }

        if(paymentRequest.amount > 10000){
            return Receipt(
                orderId = paymentRequest.orderId,
                ReceiptStatus.FLAGGED_FOR_REVIEW,
                mapOf(
                    "gateway" to "stripe-like",
                    "reason" to "High value transaction requires review",
                    "amount" to paymentRequest.amount
                )
            )
        }

        return Receipt(
            orderId = paymentRequest.orderId,
            ReceiptStatus.PAID,
            mapOf(
                "gateway" to "stripe-like",
                "transactionId" to transactionId,
                "amount" to paymentRequest.amount
            )
        )

    }
}