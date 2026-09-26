package whitekim.practice.payment.event;

public record PaymentFailedEvent(
        Long orderId
){ }
