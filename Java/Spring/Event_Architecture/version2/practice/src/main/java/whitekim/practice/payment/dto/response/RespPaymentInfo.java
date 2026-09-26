package whitekim.practice.payment.dto.response;

import whitekim.practice.payment.entity.Payment;
import whitekim.practice.payment.type.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RespPaymentInfo(
        Long memberId,
        BigDecimal chargePrice,
        PaymentStatus status,
        LocalDateTime atCreatedTime,
        LocalDateTime atFailedTime,
        LocalDateTime atConfirmTime,
        LocalDateTime atReturnTime
) {
    public static RespPaymentInfo toDto(Payment payment) {
        return new RespPaymentInfo(
                payment.getMemberId(),
                payment.getChargePrice(),
                payment.getStatus(),
                payment.getAtCreatedTime(),
                payment.getAtFailedTime(),
                payment.getAtConfirmTime(),
                payment.getAtReturnedTime()
        );
    }
}
