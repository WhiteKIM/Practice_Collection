package whitekim.practice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import whitekim.practice.common.config.AppConfig;
import whitekim.practice.common.event.OrderRequestPaymentEvent;
import whitekim.practice.common.event.PaymentFailedEvent;
import whitekim.practice.common.event.PaymentSuccessEvent;
import whitekim.practice.common.utils.IdempotencyKeyUtils;
import whitekim.practice.item.dto.request.RegisterItemForm;
import whitekim.practice.item.service.ItemService;
import whitekim.practice.member.dto.request.JoinMember;
import whitekim.practice.member.service.MemberService;
import whitekim.practice.order.dto.request.ReqOrderInfo;
import whitekim.practice.order.dto.response.RespOrderInfo;
import whitekim.practice.order.event.listener.OrderEventListener;
import whitekim.practice.order.service.OrderService;
import whitekim.practice.order.type.OrderStatus;
import whitekim.practice.payment.dto.response.RespPaymentInfo;
import whitekim.practice.payment.event.listener.PaymentEventListener;
import whitekim.practice.payment.service.PaymentService;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@EnableKafka
@Slf4j
@SpringBootTest
class PracticeKafkaTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AppConfig config;

    /* =========================== 이벤트 테스트용 ==============================*/

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoSpyBean
    private PaymentEventListener paymentEventListener;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @BeforeEach
    void setUp() {
        config.changePaymentState(true);
    }

    // 모듈 범위 확인
    @Test
    void verifyModularity() {
        ApplicationModules.of(PracticeApplication.class)
                .verify();
    }

    // 성공 이벤트 발송
    @Test
    void sendSuccessPaymentMessage() {
        JoinMember joinMember = new JoinMember("test", "test@test.com");
        RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

        Long memberId = memberService.joinMember(joinMember);
        Long itemId = itemService.registerItemInfo(itemForm);
        Long orderId = orderService.processOrder(new ReqOrderInfo(itemId, memberId, 1L));

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

                    assertThat(payments).hasSize(1);
                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.APPROVED);

                    String key = IdempotencyKeyUtils.generateKey(orderId);
                    Long paymentId = orderService.getOrderInfo(orderId).paymentId();

                    log.info("[TEST] 주문 ID : {}, 결제 ID : {}, 결제금액 : {}", orderId, paymentId, BigDecimal.valueOf(1000.00));
                    log.info("[TEST] 카프카 key : {}", key);

                    verify(orderEventListener, timeout(30000))
                            .successPaymentProcess(new PaymentSuccessEvent(orderId, paymentId, BigDecimal.valueOf(1000.00)), key);
                });
    }

    // 실패 이벤트 발송
    @Test
    void sendFailedPaymentMessage() {
        config.changePaymentState(false);

        JoinMember joinMember = new JoinMember("test", "test@test.com");
        RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

        Long memberId = memberService.joinMember(joinMember);
        Long itemId = itemService.registerItemInfo(itemForm);
        Long orderId = orderService.processOrder(new ReqOrderInfo(itemId, memberId, 1L));

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

                    assertThat(payments).hasSize(0);
                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.FAILED);

                    String key = IdempotencyKeyUtils.generateKey(orderId);
                    verify(orderEventListener, timeout(30000))
                            .failedPaymentProcess(new PaymentFailedEvent(orderId), key);
                });
    }

    // 중복 메시지 발송
    @Test
    void sendRequestDuplicatePaymentMessage() throws ExecutionException, InterruptedException, TimeoutException {
        JoinMember joinMember = new JoinMember("test", "test@test.com");
        RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

        Long memberId = memberService.joinMember(joinMember);
        Long itemId = itemService.registerItemInfo(itemForm);
        Long orderId = orderService.processOrder(new ReqOrderInfo(itemId, memberId, 1L));
        String key = IdempotencyKeyUtils.generateKey(orderId);

        // 이번트 전송 확인
        verify(paymentEventListener, timeout(30000))
                .handleProcessPayment(new OrderRequestPaymentEvent(orderId, memberId, BigDecimal.valueOf(1000)), key);

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    assertThat(payments).hasSize(1);
                });


        // 강제로 같은 메시지를 한번 더 전송
        // 주문요청 메시지 ID
        key = IdempotencyKeyUtils.generateKey(orderId);
       kafkaTemplate
               .send("order-request-payment-event", key, new OrderRequestPaymentEvent(orderId, memberId, BigDecimal.valueOf(1000)))
                    .get(10, TimeUnit.SECONDS);

        // 이벤트 중복전송 확인
        verify(paymentEventListener, timeout(30000).times(2))
                .handleProcessPayment(new OrderRequestPaymentEvent(orderId, memberId, BigDecimal.valueOf(1000)), key);

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    int totalPayAmount = payments.stream()
                            .mapToInt(p -> p.chargePrice().intValue())
                            .sum();

                    assertThat(payments).hasSize(1);
                    assertThat(totalPayAmount).isEqualTo(1000);
                });
    }

    // 결제성공 이벤트 중복 발송
    @Test
    void sendDuplicatePaymentSuccessMessage() throws ExecutionException, InterruptedException, TimeoutException {
        JoinMember joinMember = new JoinMember("test", "test@test.com");
        RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

        Long memberId = memberService.joinMember(joinMember);
        Long itemId = itemService.registerItemInfo(itemForm);
        Long orderId = orderService.processOrder(new ReqOrderInfo(itemId, memberId, 1L));

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.APPROVED);
                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    int totalPayAmount = payments.stream()
                            .mapToInt(p -> p.chargePrice().intValue())
                            .sum();

                    assertThat(payments).hasSize(1);
                    assertThat(totalPayAmount).isEqualTo(1000);
                });

        String key = IdempotencyKeyUtils.generateKey(orderId);
        Long paymentId = orderService.getOrderInfo(orderId).paymentId();

        verify(orderEventListener, timeout(30000))
                .successPaymentProcess(new PaymentSuccessEvent(orderId, paymentId, BigDecimal.valueOf(1000)), key);

        // 강제로 같은 메시지를 한번 더 전송
        // 결제성공 메시지 발송
        key = IdempotencyKeyUtils.generateKey(orderId);
        kafkaTemplate
                .send("payment-success-event", key, new PaymentSuccessEvent(orderId, paymentId, BigDecimal.valueOf(1000)))
                        .get(10, TimeUnit.SECONDS);

        verify(orderEventListener, timeout(30000).times(2))
                .successPaymentProcess(new PaymentSuccessEvent(orderId, paymentId, BigDecimal.valueOf(1000)), key);

        await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);
                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    int totalPayAmount = payments.stream()
                            .mapToInt(p -> p.chargePrice().intValue())
                            .sum();

                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.APPROVED);
                    assertThat(totalPayAmount).isEqualTo(1000);
                    assertThat(payments).hasSize(1);
                });
    }

    // 결제 실패 이벤트 중복 발송
    @Test
    void sendDuplicateFailedPaymentMessage() throws ExecutionException, InterruptedException, TimeoutException {
        config.changePaymentState(false);

        JoinMember joinMember = new JoinMember("test", "test@test.com");
        RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

        Long memberId = memberService.joinMember(joinMember);
        Long itemId = itemService.registerItemInfo(itemForm);
        Long orderId = orderService.processOrder(new ReqOrderInfo(itemId, memberId, 1L));
        String key = IdempotencyKeyUtils.generateKey(orderId);

        // 이벤트 전달 확인
        verify(orderEventListener, timeout(30000))
                .failedPaymentProcess(new PaymentFailedEvent(orderId), key);

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.FAILED);
                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    assertThat(payments).hasSize(0);
                });

        // 강제로 같은 메시지를 한번 더 전송
        // 결제 실패 메시지 발송
        key = IdempotencyKeyUtils.generateKey(orderId);
        kafkaTemplate
                .send("payment-failed-event", key, new PaymentFailedEvent(orderId))
                        .get(10, TimeUnit.SECONDS);

        // 이벤트 전달 확인
        verify(orderEventListener, timeout(30000).times(2))
                .failedPaymentProcess(new PaymentFailedEvent(orderId), key);

        await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.FAILED);
                    assertThat(payments).hasSize(0);
                });
    }

    // 결제 실패 후 성공 이벤트 발송
    @Test
    void sendFailToSuccessPaymentMessage() throws ExecutionException, InterruptedException, TimeoutException {
        config.changePaymentState(false);

        JoinMember joinMember = new JoinMember("test", "test@test.com");
        RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

        Long memberId = memberService.joinMember(joinMember);
        Long itemId = itemService.registerItemInfo(itemForm);

        Long orderId = orderService.processOrder(new ReqOrderInfo(itemId, memberId, 1L));

        // 이벤트 전달 확인
        // 결제 실패
        String key = IdempotencyKeyUtils.generateKey(orderId);
        verify(orderEventListener, timeout(30000))
                .failedPaymentProcess(new PaymentFailedEvent(orderId), key);

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.FAILED);
                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    assertThat(payments).hasSize(0);
                });

        // 주문 재시도
        // 주문 결제요청 메시지 전송
        config.changePaymentState(true);
        key = IdempotencyKeyUtils.generateKey(orderId);
        kafkaTemplate
                .send("order-request-payment-event", key, new OrderRequestPaymentEvent(orderId, memberId, BigDecimal.valueOf(1000)))
                        .get(10, TimeUnit.SECONDS);

        // 결제요청 이벤트 발행
        // 이벤트 전달 확인
        verify(paymentEventListener, timeout(30000).times(2))
                .handleProcessPayment(new OrderRequestPaymentEvent(orderId, memberId, BigDecimal.valueOf(1000)), key);
        await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.PENDING_RESOLUTION);
                    assertThat(payments).hasSize(1);
                });

        // 결제 성공 이벤트 확인
        Long paymentId = orderService.getOrderInfo(orderId).paymentId();
        key = IdempotencyKeyUtils.generateKey(orderId);
        verify(orderEventListener, timeout(30000))
                .successPaymentProcess(new PaymentSuccessEvent(orderId, paymentId, BigDecimal.valueOf(1000)), key);
    }
}
