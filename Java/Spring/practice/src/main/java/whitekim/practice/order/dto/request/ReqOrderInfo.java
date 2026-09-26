package whitekim.practice.order.dto.request;

import whitekim.practice.order.entity.Order;

public record ReqOrderInfo(
        Long itemId,
        Long memberId,
        Long purchaseCount
) {
    public Order toEntity() {
        return new Order(memberId, itemId, purchaseCount);
    }
}
