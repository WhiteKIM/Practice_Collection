package whitekim.practice.item.event.listener;

import org.springframework.stereotype.Component;

@Component
public class ItemEventListener {
    // 주문 성공 : 재고 감소

    // 주문 실패 : 재고 롤백
}
