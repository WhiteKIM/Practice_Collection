package whitekim.practice.order.event;

import lombok.Getter;

@Getter
public class OrderingFailedEvent {
    private Long orderId;

    public OrderingFailedEvent(Long orderId) {
        this.orderId = orderId;
    }
}
