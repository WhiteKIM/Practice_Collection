package whitekim.practice.payment.dto.request;

import whitekim.practice.payment.entity.Payment;

import java.math.BigDecimal;

public record ChargePaymentInfo(
        BigDecimal chargePrice,
        Long memberId
) {
    public Payment toEntity() {
        return new Payment(memberId, chargePrice);
    }
}
