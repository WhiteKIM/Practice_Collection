package whitekim.practice.order.event;

import lombok.Getter;

@Getter
public class OrderingSuccessEvent {
    private Long orderId;
    private Long paymentId;

    public OrderingSuccessEvent(Long orderId, Long paymentId) {
        this.orderId = orderId;
        this.paymentId = paymentId;
    }
}
