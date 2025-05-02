package Service.Policy;

import Model.Order;

import java.math.BigDecimal;

public class MinusDiscountPolicy extends DiscountPolicy {
    private BigDecimal discountAmount;

    public MinusDiscountPolicy() {
        this.discountAmount = BigDecimal.valueOf(1000);
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public void applyDiscount(Order order) {
        BigDecimal price = order.getPrice();
        BigDecimal discountPrice = price.subtract(discountAmount);
        order.setPrice(discountPrice);

        System.out.println("MinusDiscount Discount");
        System.out.println(price + " Minus " + discountPrice);
    }
}
