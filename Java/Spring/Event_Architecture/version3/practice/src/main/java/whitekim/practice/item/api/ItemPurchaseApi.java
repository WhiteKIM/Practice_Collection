package whitekim.practice.item.api;

import java.math.BigDecimal;

/**
 * 아이템 구매 외부공개 API
 */
public interface ItemPurchaseApi {
    BigDecimal purchase(Long itemId, Long purchaseCount);
}
