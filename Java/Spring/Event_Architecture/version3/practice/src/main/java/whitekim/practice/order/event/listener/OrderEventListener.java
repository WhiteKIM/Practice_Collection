package whitekim.practice.order.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import whitekim.practice.order.dto.response.RespOrderInfo;
import whitekim.practice.order.service.OrderService;
import whitekim.practice.common.event.PaymentFailedEvent;
import whitekim.practice.common.event.PaymentSuccessEvent;
import whitekim.practice.order.type.OrderStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    private final OrderService orderService;
    private final Set<String> keySet = ConcurrentHashMap.newKeySet();

    @KafkaListener(topics = "payment-success-event")
    public void successPaymentProcess(
            @Payload PaymentSuccessEvent successEvent,
            @Header(KafkaHeaders.RECEIVED_KEY) String successOrderKey
    ) {
        boolean isCompleteOrderPayment = orderService.getOrderStatus(successEvent.orderId());

        // 키가 이미 등록되어 있고, 주문 상태가 이미 완료된 경우
        if(keySet.contains(successOrderKey) && isCompleteOrderPayment) {
            log.info("[Payment] 이미 처리 완료된 주문입니다. | 주문 ID : {}, 결제 ID : {}, 결제 금액 : {}",
                    successEvent.orderId(),
                    successEvent.paymentId(),
                    successEvent.chargeAmount()
            );
            return;
        }

        log.info("[Payment] 결제 성공 | 주문 ID : {}, 결제 ID : {}, 결제 금액 : {}",
                successEvent.orderId(),
                successEvent.paymentId(),
                successEvent.chargeAmount()
        );
        log.info("[Payment] 결제 성공 | 카프카키 : {}", successOrderKey);

        // 주문상태 : 성공 | 결제ID : 입력
        orderService.processSuccessPayment(successEvent.orderId(), successEvent.paymentId(), successEvent.chargeAmount());
        keySet.add(successOrderKey);
    }

    @KafkaListener(topics = "payment-failed-event")
    public void failedPaymentProcess(
            @Payload PaymentFailedEvent failedEvent,
            @Header(KafkaHeaders.RECEIVED_KEY) String failedOrderKey
    ) {
        if(keySet.contains(failedOrderKey)) {
            log.info("[Payment] 이미 처리 완료된 주문입니다. | 주문 ID : {}", failedEvent.orderId());
            return;
        }

        log.info("[Payment] 결제 실패 | 주문 ID : {}", failedEvent.orderId());
        log.info("[Payment] 결제 실패 | 카프카키 : {}", failedOrderKey);

        orderService.processFailedPayment(failedEvent.orderId());
        keySet.add(failedOrderKey);
    }
}
