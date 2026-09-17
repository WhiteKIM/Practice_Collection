package whitekim.practice.item.event;

import lombok.Getter;

@Getter
public class RollbackItemStockEvent {
    private Long itemId;
    private Long itemStock;

    public RollbackItemStockEvent(Long itemId, Long itemStock) {
        this.itemId = itemId;
        this.itemStock = itemStock;
    }
}
