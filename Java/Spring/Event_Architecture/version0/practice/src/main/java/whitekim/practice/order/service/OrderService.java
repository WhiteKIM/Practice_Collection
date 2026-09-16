package whitekim.practice.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whitekim.practice.common.exception.NotExistMemberException;
import whitekim.practice.common.exception.NotExistOrderException;
import whitekim.practice.item.service.ItemService;
import whitekim.practice.member.service.MemberService;
import whitekim.practice.order.dto.request.ReqOrderInfo;
import whitekim.practice.order.dto.response.RespOrderInfo;
import whitekim.practice.order.entity.Order;
import whitekim.practice.order.repository.OrderRepository;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final MemberService memberService;
    private final ItemService itemService;

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

        // 주문 전 아이템 재고 확보
        itemService.manageInventory(orderInfo.itemId(), orderInfo.purchaseCount());

        // 주문 전 예샹결제 금액 반환
        BigDecimal purchasePrice = itemService.calPurchasePrice(orderInfo.itemId(), orderInfo.purchaseCount());

        // 청구 전송
        Long paymentId = paymentService.chargePayment(new ChargePaymentInfo(purchasePrice, orderInfo.memberId()));

        // 주문 최종 생성
        order.processPayment(paymentId, purchasePrice);

        // 주문 상태는 무조건 정상이라고 현재는 판단 => 추후 payment 상태에 따라 판단이 필요
        Order saveOrder = orderRepository.save(order);

        // @NOTE : 나중에 결제 실패 시 재고 원복하는 로직 필요 | 주문상태도 변경 필요

        return saveOrder.getId();
    }

    public List<RespOrderInfo> getAllOrderInfo() {
        return orderRepository.findAll()
                .stream()
                .map(o -> RespOrderInfo.toDto(o))
                .toList();
    }
}
