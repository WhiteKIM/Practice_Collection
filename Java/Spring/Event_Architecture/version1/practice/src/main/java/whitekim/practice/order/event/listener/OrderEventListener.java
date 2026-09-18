package whitekim.practice.order.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
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

    @EventListener
    public void failedPaymentProcess(PaymentFailedEvent failedEvent) {
        log.info("[Payment] 결제 성공 | 주문 ID : {}, 결제 ID : {}, 결제 금액 : {}",
                failedEvent.getOrderId()
        );

        orderService.processFailedPayment(failedEvent.getOrderId());
    }
}
