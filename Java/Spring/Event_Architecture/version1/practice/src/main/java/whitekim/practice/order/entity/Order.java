package whitekim.practice.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whitekim.practice.order.dto.response.RespOrderInfo;
import whitekim.practice.order.type.OrderStatus;

import java.math.BigDecimal;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private Long memberId;      // 사용자 PK
    private Long paymentId;     // 결제정보 PK
    private Long itemId;        // 아이템 PK
    
    private Long purchaseCount;         // 주문수량
    private BigDecimal purchasePrice;   // 주문금액

    public Order(Long memberId, Long itemId, Long purchaseCount) {
        this.memberId = memberId;
        this.itemId = itemId;
        this.purchaseCount = purchaseCount;
        this.orderStatus = OrderStatus.WAITING;
    }

    public void processPayment(Long paymentId, BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
        this.paymentId = paymentId;
        this.orderStatus = OrderStatus.APPROVED;    // 일단 단 성공으로 처리하자
    }

    public void changeOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
