package whitekim.practice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    /**
     * 결제 성공
     * @return
     */
    @Bean
    public NewTopic successProcessPayment() {
        return TopicBuilder.name("payment-success-event")
                .partitions(3)                          // 파티션 수 설정
                .replicas(1)                             // 복제 팩터 설정 (1)
                .config(                                            // 추가 설정
                        TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(7 * 24 * 60 * 60 * 1000L)  // 7일
                )
                .build();
    }

    /**
     * 결제 실패
     * @return
     */
    @Bean
    public NewTopic failedProcessPayment() {
        return TopicBuilder.name("payment-failed-event")
                .partitions(3)                          // 파티션 수 설정
                .replicas(1)                             // 복제 팩터 설정 (1)
                .config(                                            // 추가 설정
                        TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(7 * 24 * 60 * 60 * 1000L)  // 7일
                )
                .build();
    }

    /**
     * 주문 생성 후 결제 처리 진행
     * @return
     */
    @Bean
    public NewTopic orderRequestPayment() {
        return TopicBuilder.name("order-request-payment-event")
                .partitions(3)                          // 파티션 수 설정
                .replicas(1)                             // 복제 팩터 설정 (1)
                .config(                                            // 추가 설정
                        TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(7 * 24 * 60 * 60 * 1000L)  // 7일
                )
                .build();
    }
}
