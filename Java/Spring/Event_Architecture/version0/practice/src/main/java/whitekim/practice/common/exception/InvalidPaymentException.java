package whitekim.practice.common.exception;

public class InvalidPaymentException extends RuntimeException {
    public InvalidPaymentException() {
        super("해당 결제정보는 유효하지 않은 정보입니다.");
    }
}
