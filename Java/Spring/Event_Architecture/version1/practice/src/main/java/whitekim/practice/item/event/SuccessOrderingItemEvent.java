package whitekim.practice.item.event;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public class SuccessOrderingItemEvent {
    private Long itemId;
    private Long itemStock;

    @Setter
    private BigDecimal purchasePrice;

    public SuccessOrderingItemEvent(Long itemId, Long itemStock) {
        this.itemId = itemId;
        this.itemStock = itemStock;
    }
}
