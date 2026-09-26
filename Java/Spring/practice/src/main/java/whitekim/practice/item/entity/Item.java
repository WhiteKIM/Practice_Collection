package whitekim.practice.item.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whitekim.practice.common.exception.NotEnoughItemStockException;
import whitekim.practice.item.dto.response.RespItemInfo;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
        if(itemStock - purchaseCount < 0) {
            throw new NotEnoughItemStockException(this.itemStock, purchaseCount);
        }

        this.itemStock -= purchaseCount;
    }
}
