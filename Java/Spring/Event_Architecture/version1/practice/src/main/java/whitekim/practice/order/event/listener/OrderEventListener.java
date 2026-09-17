package whitekim.practice.order.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import whitekim.practice.order.event.OrderingFailedEvent;
import whitekim.practice.order.event.OrderingSuccessEvent;
import whitekim.practice.order.service.OrderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    private final OrderService orderService;

    // 주문 성공
    @EventListener
    public void handleOrderSuccess(OrderingSuccessEvent successEvent) {
        log.info("[ORDER] 주문 성공 | 주문 ID : {}, 결제 ID : {}", successEvent.getOrderId(), successEvent.getPaymentId());

        // 주문상태 : 성공
    }

    // 주문 실패
    @EventListener
    public void handleOrderFailed(OrderingFailedEvent failedEvent) {
        log.info("[ORDER] 주문 실패 | 주문 ID : {}", failedEvent.getOrderId());

        // 주문상태 : 실패로 처리
    }
}
