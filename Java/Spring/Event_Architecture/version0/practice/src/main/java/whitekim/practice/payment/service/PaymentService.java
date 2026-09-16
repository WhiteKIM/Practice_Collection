package whitekim.practice.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whitekim.practice.common.exception.InvalidPaymentException;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.payment.dto.response.RespPaymentInfo;
import whitekim.practice.payment.entity.Payment;
import whitekim.practice.payment.repository.PaymentRepository;
import whitekim.practice.payment.type.PaymentStatus;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public RespPaymentInfo getPaymentInfo(Long paymentId) {
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(InvalidPaymentException::new);

        return RespPaymentInfo.toDto(payment);
    }

    public Long chargePayment(ChargePaymentInfo chargeInfo) {
        Payment payment = chargeInfo.toEntity();

        // 먼저 결제청구 의뢰를 수행
        // 결제는 성공으로 왔다고 가정함
        payment.changePaymentStatus(PaymentStatus.CONFIRM);

        Payment paymentResult = paymentRepository.save(payment);

        return paymentResult.getId();
    }

    public List<RespPaymentInfo> getAllPaymentInfo() {
        return paymentRepository.findAll()
                .stream()
                .map(p -> RespPaymentInfo.toDto(p))
                .toList();
    }
}
