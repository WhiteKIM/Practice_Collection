package whitekim.practice.payment.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentRequestEvent {
    private Long orderId;
    private Long memberId;
    private BigDecimal chargeAmount;

    public PaymentRequestEvent(Long orderId, Long memberId, BigDecimal chargeAmount) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.chargeAmount = chargeAmount;
    }
}
