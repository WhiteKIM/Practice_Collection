package whitekim.practice.payment.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import whitekim.practice.payment.exception.InvalidPaymentException;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.common.event.OrderRequestPaymentEvent;
import whitekim.practice.payment.service.PaymentService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {
    private final PaymentService paymentService;
    private final Set<String> keySet = ConcurrentHashMap.newKeySet();

    // 결제처리 진행
    @KafkaListener(topics = "order-request-payment-event")
    public void handleProcessPayment(
            @Payload OrderRequestPaymentEvent requestEvent,
            @Header(KafkaHeaders.RECEIVED_KEY) String paymentKey
    ) {
        log.info("[Payment] 결제 처리 진행 | 주문ID : {}, 청구금액 : {}", requestEvent.orderId(), requestEvent.chargeAmount());
        log.info("[Payment] 결제 처리 진행 | 카프카키 : {}", paymentKey);

        if(keySet.contains(paymentKey)) {
            // 이미 처리된 진행
            log.info("[Payment] 이미 결제가 완료된 주문정보입니다. | 주문ID : {}, 청구금액 : {}", requestEvent.orderId(), requestEvent.chargeAmount());
            return;
        }

        try {
            paymentService.chargePayment(new ChargePaymentInfo(requestEvent.chargeAmount(), requestEvent.memberId(), requestEvent.orderId()));
            keySet.add(paymentKey); // 키 저장
        } catch (InvalidPaymentException e) {
            log.info("[Payment] 결제 처리 진행 중 오류 발생");
        }
    }
}
