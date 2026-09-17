package whitekim.practice.order.event;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public class RequestOrderPayment {
    private Long orderId;
    private Long memeberId;
    @Setter
    private Long paymentId; // 값을 페이먼트에서 받아야 함
    private BigDecimal chargeAmount;

    public RequestOrderPayment(BigDecimal chargeAmount, Long orderId, Long memeberId) {
        this.chargeAmount = chargeAmount;
        this.orderId = orderId;
        this.memeberId = memeberId;
    }
}
