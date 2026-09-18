package whitekim.practice.payment.event;

import lombok.Getter;

@Getter
public class PaymentFailedEvent {
    private Long orderId;

    public PaymentFailedEvent(Long orderId) {
        this.orderId = orderId;
    }
}
