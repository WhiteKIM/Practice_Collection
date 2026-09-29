package whitekim.practice.common.event;

import java.math.BigDecimal;


public record OrderRequestPaymentEvent(
        Long orderId,
        Long memberId,
        BigDecimal chargeAmount
) {
}
