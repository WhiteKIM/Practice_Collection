package whitekim.practice.order.type;

public enum OrderStatus {
    WAITING,
    APPROVED,
    DELIVERY,
    SUCCESS,
    FAILED,
    STOCK_RESERVATION_FAILED,
    PENDING_RESOLUTION,         // 주문 확인 후 처리
    UNKNOWN
}
