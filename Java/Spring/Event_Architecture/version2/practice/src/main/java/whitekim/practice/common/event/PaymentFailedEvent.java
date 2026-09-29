package whitekim.practice.common.event;

public record PaymentFailedEvent(
        Long orderId
){ }
