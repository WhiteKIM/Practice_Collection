package whitekim.practice.common.exception;

public class NotExistOrderException extends RuntimeException {
    public NotExistOrderException(Long orderId) {
        super("Order Id : " + orderId + "는 존재하지 않는 주문 정보입니다.");
    }
}
