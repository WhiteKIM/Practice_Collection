package whitekim.practice.item.dto.response;

import whitekim.practice.item.entity.Item;

import java.math.BigDecimal;

public record RespItemInfo(
        Long itemStock,
        String itemName,
        BigDecimal itemPrice
) {
    public static RespItemInfo toDto(Item item) {
        return new RespItemInfo(item.getItemStock(), item.getItemName(), item.getPrice());
    }
}
