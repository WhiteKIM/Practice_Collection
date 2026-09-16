package whitekim.practice.common.exception;

public class NotExistItemException extends RuntimeException {
    public NotExistItemException() {
        super("해당 상품은 현재 사용할 수 없는 상품입니다.");
    }
}
