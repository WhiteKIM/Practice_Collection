package whitekim.practice.order.dto.response;

import whitekim.practice.order.entity.Order;
import whitekim.practice.order.type.OrderStatus;

import java.math.BigDecimal;

public record RespOrderInfo(
        Long memberId,
        Long paymentId,
        Long itemId,
        Long purchaseCount,
        BigDecimal purchasePrice,
        OrderStatus orderStatus
) {
    public static RespOrderInfo toDto(Order order) {
        return new RespOrderInfo(
                order.getMemberId(),
                order.getPaymentId(),
                order.getItemId(),
                order.getPurchaseCount(),
                order.getPurchasePrice(),
                order.getOrderStatus()
        );
    }
}
