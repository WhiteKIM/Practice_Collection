package whitekim.practice.payment.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import whitekim.practice.payment.exception.InvalidPaymentException;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.common.event.OrderRequestPaymentEvent;
import whitekim.practice.payment.service.PaymentService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {
    private final PaymentService paymentService;

    // 결제처리 진행
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @KafkaListener(topics = "order-request-payment-event")
    public void handleProcessPayment(OrderRequestPaymentEvent requestEvent) {
        log.info("[Payment] 결제 처리 진행 | 주문ID : {}, 청구금액 : {}", requestEvent.orderId(), requestEvent.chargeAmount());
        try {
            paymentService.chargePayment(new ChargePaymentInfo(requestEvent.chargeAmount(), requestEvent.memberId(), requestEvent.orderId()));
        } catch (InvalidPaymentException e) {
            log.info("[Payment] 결제 처리 진행 중 오류 발생");
        }
    }
}
