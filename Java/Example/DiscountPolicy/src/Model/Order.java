package Model;

import Service.Policy.DiscountPolicy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private final String orderId;
    private final BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private List<Item> itemList = new ArrayList<>();

    public User getBuyer() {
        return buyer;
    }

    private User buyer;
    private final DiscountPolicy discountPolicy;

    public Order(List<Item> itemList, User buyer, DiscountPolicy discountPolicy) {
        this.buyer = buyer;
        this.orderId = UUID.randomUUID().toString();
        this.itemList = itemList;
        this.discountPolicy = discountPolicy;
        this.originalPrice = BigDecimal.valueOf(itemList.stream().mapToInt(Item::getPrice).sum());
        this.discountPrice = originalPrice;

        System.out.println(originalPrice + " " + discountPrice);

        discountPolicy.handle(this);
    }

    public BigDecimal getPrice() {
        return discountPrice;
    }

    public void setPrice(BigDecimal price) {
        this.discountPrice = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<Item> getItemList() {
        return itemList;
    }
}
