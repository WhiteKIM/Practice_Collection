package whitekim.practice.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import whitekim.practice.common.config.AppConfig;
import whitekim.practice.payment.exception.InvalidPaymentException;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.payment.dto.response.RespPaymentInfo;
import whitekim.practice.payment.entity.Payment;
import whitekim.practice.common.event.PaymentFailedEvent;
import whitekim.practice.common.event.PaymentSuccessEvent;
import whitekim.practice.payment.repository.PaymentRepository;
import whitekim.practice.payment.type.PaymentStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AppConfig config;

    // phase 3
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RespPaymentInfo getPaymentInfo(Long paymentId) {
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(InvalidPaymentException::new);

        return RespPaymentInfo.toDto(payment);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void chargePayment(ChargePaymentInfo chargeInfo) {
        String nowTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if(!config.isAvailableProcessPayment()) {
            // 메시지 키 생성
            String key = new StringJoiner("-")
                    .add(nowTime)
                    .add(Long.toString(chargeInfo.orderId()))
                    .toString();

            kafkaTemplate.send("payment-failed-event", key, new PaymentFailedEvent(chargeInfo.orderId()));

            throw new InvalidPaymentException("현재 결제를 처리할 수 없습니다.");
        }

        Payment payment = chargeInfo.toEntity();
        payment.changePaymentStatus(PaymentStatus.CONFIRM);

        // 메시지 키 생성
        Payment paymentResult = paymentRepository.save(payment);

        String key = new StringJoiner("-")
                .add(nowTime)
                .add(Long.toString(chargeInfo.orderId()))
                .add(Long.toString(paymentResult.getId()))
                .toString();

        kafkaTemplate.send("payment-success-event", key, new PaymentSuccessEvent(chargeInfo.orderId(), paymentResult.getId(), chargeInfo.chargePrice()));
    }

    public List<RespPaymentInfo> getAllPaymentInfo() {
        return paymentRepository.findAll()
                .stream()
                .map(RespPaymentInfo::toDto)
                .toList();
    }
}
