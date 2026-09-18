package whitekim.practice.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import whitekim.practice.common.exception.NotExistMemberException;
import whitekim.practice.common.exception.NotExistOrderException;
import whitekim.practice.item.service.ItemService;
import whitekim.practice.member.service.MemberService;
import whitekim.practice.order.dto.request.ReqOrderInfo;
import whitekim.practice.order.dto.response.RespOrderInfo;
import whitekim.practice.order.entity.Order;
import whitekim.practice.order.repository.OrderRepository;
import whitekim.practice.order.type.OrderStatus;
import whitekim.practice.payment.event.PaymentRequestEvent;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final MemberService memberService;
    private final ItemService itemService;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher publisher;

    public RespOrderInfo getOrderInfo(Long orderId) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                   new NotExistOrderException(orderId)
                );

        return RespOrderInfo.toDto(order);
    }

    public Long processOrder(ReqOrderInfo orderInfo) {
        // 멤버 여부 확인
        boolean isExist = memberService.existMemberById(orderInfo.memberId());

        // 없는데 계속진행하려고?
        if(!isExist) {
            throw new NotExistMemberException();
        }

        Order order = orderInfo.toEntity();
        order = orderRepository.save(order);

        // 주문 전 아이템 재고 확보
        // 주문 전 예샹결제 금액 반환
        BigDecimal purchasePrice = itemService.purchase(orderInfo.itemId(), orderInfo.purchaseCount());

        // 청구 전송
        publisher.publishEvent(new PaymentRequestEvent(order.getId(), order.getMemberId(), purchasePrice));

        return order.getId();
    }

    public List<RespOrderInfo> getAllOrderInfo() {
        return orderRepository.findAll()
                .stream()
                .map(o -> RespOrderInfo.toDto(o))
                .toList();
    }

    public void processSuccessPayment(Long orderId, Long paymentId, BigDecimal chargeAmount) {
        Order order =
                orderRepository.findById(orderId).orElseThrow(() -> new NotExistOrderException(orderId));

        // 주문 최종 생성
        order.processPayment(paymentId, chargeAmount);
    }

    public void processFailedPayment(Long orderId) {
        Order order =
                orderRepository.findById(orderId).orElseThrow(() -> new NotExistOrderException(orderId));

        // 주문상태 업데이트 : 실패
        order.changeOrderStatus(OrderStatus.FAILED);
    }
}
