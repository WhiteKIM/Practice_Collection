package whitekim.practice.item.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whitekim.practice.item.exception.InvalidPurchaseException;
import whitekim.practice.item.exception.NotEnoughItemStockException;

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

    /**
     * 재고 확보 처리
     * @param purchaseCount - 결제 완료 전 사용가능한 재고를 확인 후 확보
     */
    public void manageInventory(Long purchaseCount) {
        if(purchaseCount <= 0) {
            throw new InvalidPurchaseException(purchaseCount);
        }

        if(itemStock - purchaseCount < 0) {
            throw new NotEnoughItemStockException(this.itemStock, purchaseCount);
        }

        this.itemStock -= purchaseCount;
    }

    /**
     * 재고 원복 처리
     * @param rollbackCount - 원복해야 할 재고 수
     */
    public void rollbackInventory(Long rollbackCount) {
        if(rollbackCount <= 0) {
            throw new InvalidPurchaseException(rollbackCount);
        }

        this.itemStock += rollbackCount;
    }
}
