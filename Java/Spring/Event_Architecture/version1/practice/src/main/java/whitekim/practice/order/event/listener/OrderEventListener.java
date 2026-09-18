package whitekim.practice.order.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import whitekim.practice.order.service.OrderService;
import whitekim.practice.payment.event.PaymentFailedEvent;
import whitekim.practice.payment.event.PaymentSuccessEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    private final OrderService orderService;

    @EventListener
    public void successPaymentProcess(PaymentSuccessEvent successEvent) {
        log.info("[Payment] 결제 성공 | 주문 ID : {}, 결제 ID : {}, 결제 금액 : {}",
                successEvent.getOrderId(),
                successEvent.getPaymentId(),
                successEvent.getChargeAmount()
        );

        // 주문상태 : 성공 | 결제ID : 입력
        orderService.processSuccessPayment(successEvent.getOrderId(), successEvent.getPaymentId(), successEvent.getChargeAmount());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void failedPaymentProcess(PaymentFailedEvent failedEvent) {
        log.info("[Payment] 결제 실패 | 주문 ID : {}", failedEvent.getOrderId());

        orderService.processFailedPayment(failedEvent.getOrderId());
    }
}
