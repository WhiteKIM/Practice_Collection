package whitekim.practice.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import whitekim.practice.common.config.AppConfig;
import whitekim.practice.common.exception.InvalidPaymentException;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.payment.dto.response.RespPaymentInfo;
import whitekim.practice.payment.entity.Payment;
import whitekim.practice.payment.event.PaymentFailedEvent;
import whitekim.practice.payment.event.PaymentSuccessEvent;
import whitekim.practice.payment.repository.PaymentRepository;
import whitekim.practice.payment.type.PaymentStatus;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AppConfig config;
    private final ApplicationEventPublisher publisher;

    public RespPaymentInfo getPaymentInfo(Long paymentId) {
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(InvalidPaymentException::new);

        return RespPaymentInfo.toDto(payment);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long chargePayment(ChargePaymentInfo chargeInfo) {
        if(!config.isAvailableProcessPayment()) {
            publisher.publishEvent(new PaymentFailedEvent(chargeInfo.orderId()));
            throw new InvalidPaymentException("현재 결제를 처리할 수 없습니다.");
        }

        Payment payment = chargeInfo.toEntity();
        payment.changePaymentStatus(PaymentStatus.CONFIRM);

        Payment paymentResult = paymentRepository.save(payment);
        publisher.publishEvent(new PaymentSuccessEvent(chargeInfo.orderId(), paymentResult.getId(), chargeInfo.chargePrice()));

        return paymentResult.getId();
    }

    public List<RespPaymentInfo> getAllPaymentInfo() {
        return paymentRepository.findAll()
                .stream()
                .map(p -> RespPaymentInfo.toDto(p))
                .toList();
    }
}
