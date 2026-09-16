package whitekim.practice.common.exception;

public class InvalidPurchaseException extends RuntimeException {
    public InvalidPurchaseException(Long purchaseCount) {
        super("부정확한 구매정보입니다. 확인 후 재입력바랍니다. (구매요청 수량은 0보다 큰 값을 입력하시오. 현재 구매수량 = " + purchaseCount + ")");
    }
}
