package whitekim.practice.payment.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import whitekim.practice.order.event.RequestOrderPayment;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.payment.event.PaymentFailedEvent;
import whitekim.practice.payment.event.PaymentSuccessEvent;
import whitekim.practice.payment.service.PaymentService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {
    private final PaymentService paymentService;

    // 결제 성공
    @EventListener
    public void handlePaymentSuccess(PaymentSuccessEvent successEvent) {
        log.info("[Payment] 결제 성공 | 주문 ID : {}, 결제 ID : {}, 결제 금액 : {}",
                successEvent.getOrderId(),
                successEvent.getPaymentId(),
                successEvent.getChargeAmount()
        );
    }

    // 결제 실패
    @EventListener
    public void handlePaymentFailed(PaymentFailedEvent failedEvent) {
        log.info("[Payment] 결제 실패 | 주문 ID : {}, 청구 금액 : {}",
                failedEvent.getOrderId(),
                failedEvent.getChargeAmount()
        );
    }

    // 결제처리 진행
    @EventListener
    public void handleProcessPayment(RequestOrderPayment orderPayment) {
        log.info("[Payment] 결제 처리 진행 | 주문ID : {}, 청구금액 : {}", orderPayment.getOrderId(), orderPayment.getChargeAmount());
        Long paymentId =
                paymentService.chargePayment(new ChargePaymentInfo(orderPayment.getChargeAmount(), orderPayment.getMemeberId()));

        orderPayment.setPaymentId(paymentId);
    }
}
