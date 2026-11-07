package whitekim.practice.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

public class IdempotencyKeyUtils {
    // 주문 기반 멱등키 생성
    public static String generateKey(Long orderId) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        StringJoiner joiner = new StringJoiner("-");

        return joiner
                .add(now)
                .add(Long.toString(orderId))
                .toString();
    }
}
