package whitekim.practice.item.api;

/**
 * 아이템 재고 원복 공개 API
 */
public interface ItemRollbackApi {
    void rollbackInventory(Long itemId, Long purchaseCount);
}
