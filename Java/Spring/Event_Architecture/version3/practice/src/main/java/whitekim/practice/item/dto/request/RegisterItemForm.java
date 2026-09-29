package whitekim.practice.item.dto.request;

import whitekim.practice.item.entity.Item;

import java.math.BigDecimal;

public record RegisterItemForm(
        Long itemStock,
        String itemName,
        BigDecimal itemPrice
) {
    public RegisterItemForm {
        if(itemStock <= 0) {
            throw new IllegalArgumentException("재고는 최소한 0보다 큰 수를 가져야 합니다.");
        }

        if(itemPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("상품 가격은 최소한 0원보다는 더 크게 설정하십시오. (이벤트 상품은 관리자 문의)");
        }
    }

    public Item toEntity() {
        return new Item(itemStock, itemName, itemPrice);
    }
}
