package whitekim.practice.item.dto.request;

import whitekim.practice.item.entity.Item;

import java.math.BigDecimal;

public record RegisterItemForm(
        Long itemStock,
        String itemName,
        BigDecimal itemPrice
) {
    public Item toEntity() {
        return new Item(itemStock, itemName, itemPrice);
    }
}
