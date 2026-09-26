package whitekim.practice.common.exception;

public class NotEnoughItemStockException extends RuntimeException {
    public NotEnoughItemStockException(Long itemStock, Long requestItemStock) {
        super("현재 사용가능한 재고는 " + itemStock + "입니다. 요청하신 수량은 " + requestItemStock + "입니다");
    }
}
