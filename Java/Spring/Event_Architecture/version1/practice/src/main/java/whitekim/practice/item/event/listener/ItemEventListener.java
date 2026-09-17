package whitekim.practice.item.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import whitekim.practice.item.dto.response.RespItemInfo;
import whitekim.practice.item.event.RollbackItemStockEvent;
import whitekim.practice.item.event.SuccessOrderingItemEvent;
import whitekim.practice.item.service.ItemService;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemEventListener {
    private final ItemService itemService;

    // 주문 성공 : 재고 감소
    @EventListener
    public void handleDecreaseItemStock(SuccessOrderingItemEvent itemEvent) {
        BigDecimal purchasePrice = itemService.purchase(itemEvent.getItemId(), itemEvent.getItemStock());
        RespItemInfo itemInfo = itemService.getItemInfo(itemEvent.getItemId());
        itemEvent.setPurchasePrice(purchasePrice);

        log.info("[ITEM] 상품 주문 성공 | 아이템 ID : {}, 주문 수량 : {}, 재고 : {}",
                itemEvent.getItemId(),
                itemEvent.getItemStock(),
                itemInfo.itemStock()
        );
    }

    // 주문 실패 : 재고 롤백
    @EventListener
    public void handleRollbackItemStock(RollbackItemStockEvent itemStockEvent) {
        log.info("[ITEM] 주문 처리 중 오류, 재고 롤백 | 아이템 ID : {}, 롤백 수량 : {}", itemStockEvent.getItemId(), itemStockEvent.getItemStock());

        // @NOTE : 함수나 기능은 수정이 필요함
        itemService.purchase(itemStockEvent.getItemId(), itemStockEvent.getItemStock());
    }
}
