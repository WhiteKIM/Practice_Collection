package whitekim.practice.payment.event;

import java.math.BigDecimal;

public record PaymentSuccessEvent(
        Long orderId,
        Long paymentId,
        BigDecimal chargeAmount
) {
}
