package whitekim.practice.order.dto.request;

import whitekim.practice.order.entity.Order;

public record ReqOrderInfo(
        Long itemId,
        Long memberId,
        Long purchaseCount
) {
    public ReqOrderInfo {
        if(purchaseCount <= 0) {
            throw new IllegalArgumentException("최소 주문 수량은 0보다 큰 값으로 설정해야 합니다.");
        }
    }

    public Order toEntity() {
        return new Order(memberId, itemId, purchaseCount);
    }
}
