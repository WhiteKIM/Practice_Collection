package whitekim.practice.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.payment.dto.response.RespPaymentInfo;
import whitekim.practice.payment.type.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime atCreatedTime;
    private LocalDateTime atFailedTime;
    private LocalDateTime atConfirmTime;
    private LocalDateTime atReturnedTime;

    private Long memberId;
    private BigDecimal chargePrice;

    public Payment(Long memberId, BigDecimal chargePrice) {
        this.memberId = memberId;
        this.chargePrice = chargePrice;
        this.status = PaymentStatus.STANDBY;
        this.atCreatedTime = LocalDateTime.now();
    }

    public void changePaymentStatus(PaymentStatus status) {
        this.status = status;

        switch (status) {
            case PaymentStatus.FAILED -> this.atFailedTime = LocalDateTime.now();
            case PaymentStatus.CONFIRM -> this.atConfirmTime = LocalDateTime.now();
            case PaymentStatus.RETURNED -> this.atReturnedTime = LocalDateTime.now();
        }
    }
}
