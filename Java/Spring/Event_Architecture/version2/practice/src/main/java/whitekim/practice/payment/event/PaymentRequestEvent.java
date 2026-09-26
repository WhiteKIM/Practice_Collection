package whitekim.practice.payment.event;

import java.math.BigDecimal;


public record PaymentRequestEvent(
        Long orderId,
        Long memberId,
        BigDecimal chargeAmount
) {
}
