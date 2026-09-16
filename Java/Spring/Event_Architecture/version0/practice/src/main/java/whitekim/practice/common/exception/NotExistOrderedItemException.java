package whitekim.practice.common.exception;

public class NotExistOrderedItemException extends RuntimeException {
    public NotExistOrderedItemException() {
        super("해당 상품은 현재 주문할 수 없는 상품입니다.");
    }
}
