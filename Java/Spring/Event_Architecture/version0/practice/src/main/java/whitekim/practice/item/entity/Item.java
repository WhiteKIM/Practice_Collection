package whitekim.practice.item.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whitekim.practice.common.exception.InvalidPurchaseException;
import whitekim.practice.common.exception.NotEnoughItemStockException;
import whitekim.practice.item.dto.response.RespItemInfo;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "tb_item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long itemStock;
    private String itemName;
    private BigDecimal price;

    public Item(Long itemStock, String itemName, BigDecimal price) {
        this.itemStock = itemStock;
        this.itemName = itemName;
        this.price = price;
    }

    public void manageInventory(Long purchaseCount) {
        if(purchaseCount <= 0) {
            throw new InvalidPurchaseException(purchaseCount);
        }

        if(itemStock - purchaseCount < 0) {
            throw new NotEnoughItemStockException(this.itemStock, purchaseCount);
        }

        this.itemStock -= purchaseCount;
    }
}
