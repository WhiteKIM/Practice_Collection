package whitekim.practice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import whitekim.practice.common.config.AppConfig;
import whitekim.practice.common.event.OrderRequestPaymentEvent;
import whitekim.practice.item.dto.request.RegisterItemForm;
import whitekim.practice.item.service.ItemService;
import whitekim.practice.member.dto.request.JoinMember;
import whitekim.practice.member.service.MemberService;
import whitekim.practice.order.dto.request.ReqOrderInfo;
import whitekim.practice.order.dto.response.RespOrderInfo;
import whitekim.practice.order.service.OrderService;
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

    @Test
    void sendDuplicateMessage() {
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
        String nowTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = new StringJoiner("-")
                .add(nowTime)
                .add(Long.toString(orderId))
                .add(Long.toString(memberId))
                .toString();

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
}
