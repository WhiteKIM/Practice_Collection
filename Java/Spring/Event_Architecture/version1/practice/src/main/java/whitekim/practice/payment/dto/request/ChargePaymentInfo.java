package whitekim.practice.payment.dto.request;

import whitekim.practice.payment.entity.Payment;

import java.math.BigDecimal;

public record ChargePaymentInfo(
        BigDecimal chargePrice,
        Long memberId
) {
    public ChargePaymentInfo {
        // 0원 이벤트 상품 포함 결제처리
        if(chargePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("최소 결제금액은 양수값이여야 합니다.");
        }
    }

    public Payment toEntity() {
        return new Payment(memberId, chargePrice);
    }
}
