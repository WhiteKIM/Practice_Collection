package whitekim.practice.common.event;

import java.math.BigDecimal;

public record PaymentSuccessEvent(
        Long orderId,
        Long paymentId,
        BigDecimal chargeAmount
) {
}
