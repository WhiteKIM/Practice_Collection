package whitekim.practice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whitekim.practice.common.exception.InvalidPaymentException;
import whitekim.practice.common.exception.NotEnoughItemStockException;
import whitekim.practice.item.dto.request.RegisterItemForm;
import whitekim.practice.item.dto.response.RespItemInfo;
import whitekim.practice.item.service.ItemService;
import whitekim.practice.member.dto.request.JoinMember;
import whitekim.practice.member.service.MemberService;
import whitekim.practice.order.dto.request.ReqOrderInfo;
import whitekim.practice.order.dto.response.RespOrderInfo;
import whitekim.practice.order.service.OrderService;
import whitekim.practice.order.type.OrderStatus;
import whitekim.practice.payment.dto.response.RespPaymentInfo;
import whitekim.practice.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PracticeApplicationTests {
	@Autowired
	private ItemService itemService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private PaymentService paymentService;

	/**
	 * ① 재고 차감 성공
	 * 재고 10
	 * 주문 2
	 * → 재고 8
	 */
	@Test
	void scenario1() {
		// 데이터 생성
		JoinMember joinMember = new JoinMember("test", "test@test.com");
		RegisterItemForm itemForm = new RegisterItemForm(10L, "사과", BigDecimal.valueOf(1000));

		Long memberId = memberService.joinMember(joinMember);
		Long itemId = itemService.registerItemInfo(itemForm);

		ReqOrderInfo reqOrderInfo = new ReqOrderInfo(itemId, memberId, 2L);
		orderService.processOrder(reqOrderInfo);

		RespItemInfo itemInfo = itemService.getItemInfo(itemId);
		assertThat(itemInfo.itemStock()).isEqualTo(8L);
	}

	/**
	 *  ② 재고 부족
	 * 	 재고 1
	 * 	 주문 2
	 * 	 → NotEnoughItemStockException
	 */
	@Test
	void scenario2() {
		JoinMember joinMember = new JoinMember("test", "test@test.com");
		RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

		Long memberId = memberService.joinMember(joinMember);
		Long itemId = itemService.registerItemInfo(itemForm);

		ReqOrderInfo reqOrderInfo = new ReqOrderInfo(itemId, memberId, 2L);
		assertThatThrownBy(() -> orderService.processOrder(reqOrderInfo))
				.isInstanceOf(NotEnoughItemStockException.class);
	}

	/**
	 * ③ 정상 주문
	 * → Payment 생성
	 * → paymentId 존재
	 * → purchasePrice 존재
	 * → OrderStatus APPROVED
	 * → 재고 감소
	 */
	@Test
	void scenario3() {
		JoinMember joinMember = new JoinMember("test", "test@test.com");
		RegisterItemForm itemForm = new RegisterItemForm(10L, "사과", BigDecimal.valueOf(1000));

		Long memberId = memberService.joinMember(joinMember);
		Long itemId = itemService.registerItemInfo(itemForm);

		ReqOrderInfo reqOrderInfo = new ReqOrderInfo(itemId, memberId, 2L);
		Long orderId = orderService.processOrder(reqOrderInfo);

		RespItemInfo itemInfo = itemService.getItemInfo(itemId);
		RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);
		RespPaymentInfo paymentInfo = paymentService.getPaymentInfo(orderInfo.paymentId());

		// 재고 정상감소
		assertThat(itemInfo.itemStock()).isEqualTo(8L);

		// 결제 처리 성공 및 결제금액 확인
		assertThat(paymentInfo.chargePrice())
				.isEqualByComparingTo(BigDecimal.valueOf(2000));

		// 주문상태 : 승인
		assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.APPROVED);
	}

	/**
	 * ④ 주문 실패 시 rollback
	 * 예외 발생
	 * → Order 생성 안 됨
	 * → Payment 생성 안 됨
	 * → 재고 유지
	 */
	@Test
	void scenario4() {
		// 데이터 생성
		// 데이터 생성
		JoinMember joinMember = new JoinMember("test", "test@test.com");
		RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

		Long memberId = memberService.joinMember(joinMember);
		Long itemId = itemService.registerItemInfo(itemForm);

		ReqOrderInfo reqOrderInfo = new ReqOrderInfo(itemId, memberId, 2L);
		assertThatThrownBy(() -> orderService.processOrder(reqOrderInfo))
				.isInstanceOf(NotEnoughItemStockException.class);

		RespItemInfo itemInfo = itemService.getItemInfo(itemId);

		List<RespOrderInfo> allOrderInfo = orderService.getAllOrderInfo();
		List<RespPaymentInfo> allPaymentInfo = paymentService.getAllPaymentInfo();

		assertThat(allOrderInfo.size()).isEqualTo(0);	// 생성된 주문이 있으면 안돼요
		assertThat(allPaymentInfo.size()).isEqualTo(0);	// 생성된 결제정보는 있으면 안돼요

		// 수량 변함 없음
		assertThat(itemInfo.itemStock()).isEqualTo(1L);
	}

	/**
	 * ⑤ 잘못된 주문 수량
	 * 0 또는 음수
	 * → 주문 거부
	 * → 재고 변하지 않음
	 */
	@Test
	void scenario5() {
		// 데이터 생성
		JoinMember joinMember = new JoinMember("test", "test@test.com");
		RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

		Long memberId = memberService.joinMember(joinMember);
		Long itemId = itemService.registerItemInfo(itemForm);

		assertThatThrownBy(() -> new ReqOrderInfo(itemId, memberId, -2L))
				.isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(() -> new ReqOrderInfo(itemId, memberId, 0L))
				.isInstanceOf(IllegalArgumentException.class);

		RespItemInfo itemInfo = itemService.getItemInfo(itemId);
		assertThat(itemInfo.itemStock()).isEqualTo(1L);
	}
}