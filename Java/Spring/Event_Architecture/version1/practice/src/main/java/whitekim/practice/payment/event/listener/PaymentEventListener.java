package whitekim.practice.payment.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.payment.event.PaymentRequestEvent;
import whitekim.practice.payment.service.PaymentService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {
    private final PaymentService paymentService;

    // 결제처리 진행
    @EventListener
    public void handleProcessPayment(PaymentRequestEvent requestEvent) {
        log.info("[Payment] 결제 처리 진행 | 주문ID : {}, 청구금액 : {}", requestEvent.getOrderId(), requestEvent.getChargeAmount());
        paymentService.chargePayment(new ChargePaymentInfo(requestEvent.getChargeAmount(), requestEvent.getMemberId(), requestEvent.getOrderId()));
    }
}
