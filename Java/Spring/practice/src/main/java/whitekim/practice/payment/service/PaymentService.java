package whitekim.practice.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.payment.dto.response.RespPaymentInfo;
import whitekim.practice.payment.entity.Payment;
import whitekim.practice.payment.repository.PaymentRepository;
import whitekim.practice.payment.type.PaymentStatus;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public RespPaymentInfo getPaymentInfo(Long paymentId) {
        Optional<Payment> optPayment = paymentRepository.findById(paymentId);

        if(optPayment.isEmpty()) {
            throw new RuntimeException();
        }

        return RespPaymentInfo.toDto(optPayment.get());
    }

    public Long chargePayment(ChargePaymentInfo chargeInfo) {
        Payment payment = chargeInfo.toEntity();

        // 먼저 결제청구 의뢰를 수행
        // 결제는 성공으로 왔다고 가정함
        payment.changePaymentStatus(PaymentStatus.CONFIRM);

        Payment paymentResult = paymentRepository.save(payment);

        return paymentResult.getId();
    }
}
