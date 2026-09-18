package whitekim.practice.payment.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentFailedEvent {
    private Long orderId;

    public PaymentFailedEvent(Long orderId, Long paymentId, BigDecimal chargeAmount) {
        this.orderId = orderId;
    }
}
