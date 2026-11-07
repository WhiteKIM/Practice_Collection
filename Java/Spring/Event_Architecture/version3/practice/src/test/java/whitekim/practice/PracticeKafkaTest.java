package whitekim.practice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.modulith.core.ApplicationModules;
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
import whitekim.practice.order.service.OrderService;
import whitekim.practice.order.type.OrderStatus;
import whitekim.practice.payment.dto.response.RespPaymentInfo;
import whitekim.practice.payment.service.PaymentService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@EnableKafka
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

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

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
                });
    }

    // 중복 메시지 발송
    @Test
    void sendRequestDuplicatePaymentMessage() {
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

                    assertThat(payments).hasSize(1);
                });


        // 강제로 같은 메시지를 한번 더 전송
        // 주문요청 메시지 ID
        String key = IdempotencyKeyUtils.generateKey(orderId);
        kafkaTemplate.send("order-request-payment-event", key, new OrderRequestPaymentEvent(orderId, memberId, BigDecimal.valueOf(1000)));

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
    void sendDuplicatePaymentSuccessMessage() {
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

                    Long paymentId = orderInfo.paymentId();

                    // 강제로 같은 메시지를 한번 더 전송
                    // 결제성공 메시지 발송
                    String key = IdempotencyKeyUtils.generateKey(orderId);
                    kafkaTemplate.send("payment-success-event", key, new PaymentSuccessEvent(orderId, paymentId, orderInfo.purchasePrice()));
                });

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
    void sendDuplicateFailedPaymentMessage() {
        config.changePaymentState(false);

        JoinMember joinMember = new JoinMember("test", "test@test.com");
        RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

        Long memberId = memberService.joinMember(joinMember);
        Long itemId = itemService.registerItemInfo(itemForm);

        Long orderId = orderService.processOrder(new ReqOrderInfo(itemId, memberId, 1L));

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.FAILED);
                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    assertThat(payments).hasSize(0);

                    // 강제로 같은 메시지를 한번 더 전송
                    // 결제 실패 메시지 발송
                    String key = IdempotencyKeyUtils.generateKey(orderId);
                    kafkaTemplate.send("payment-failed-event", key, new PaymentFailedEvent(orderId));
                });

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
    void sendFailToSuccessPaymentMessage() {
        config.changePaymentState(false);

        JoinMember joinMember = new JoinMember("test", "test@test.com");
        RegisterItemForm itemForm = new RegisterItemForm(1L, "사과", BigDecimal.valueOf(1000));

        Long memberId = memberService.joinMember(joinMember);
        Long itemId = itemService.registerItemInfo(itemForm);

        Long orderId = orderService.processOrder(new ReqOrderInfo(itemId, memberId, 1L));

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.FAILED);
                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    assertThat(payments).hasSize(0);

                    // 주문 재시도
                    // 주문 결제요청 메시지 전송
                    config.changePaymentState(true);
                    String key = IdempotencyKeyUtils.generateKey(orderId);
                    kafkaTemplate.send("order-request-payment-event", key, new OrderRequestPaymentEvent(orderId, memberId, BigDecimal.valueOf(1000)));
                });

        await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

                    List<RespPaymentInfo> payments =
                            paymentService.getAllPaymentInfo();

                    assertThat(orderInfo.orderStatus()).isEqualTo(OrderStatus.PENDING_RESOLUTION);
                    assertThat(payments).hasSize(1);
                });
    }
}
