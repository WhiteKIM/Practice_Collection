package whitekim.practice.payment.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentFailedEvent {
    private Long orderId;
    private BigDecimal chargeAmount;

    public PaymentFailedEvent(Long orderId, BigDecimal chargeAmount) {
        this.orderId = orderId;
        this.chargeAmount = chargeAmount;
    }
}
