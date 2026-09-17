package whitekim.practice.payment.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentSuccessEvent {
    private Long orderId;
    private Long paymentId;
    private BigDecimal chargeAmount;

    public PaymentSuccessEvent(Long orderId, Long paymentId, BigDecimal chargeAmount) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.chargeAmount = chargeAmount;
    }
}
