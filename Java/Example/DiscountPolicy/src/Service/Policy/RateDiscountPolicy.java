package Service.Policy;

import Model.Order;

import java.math.BigDecimal;

public class RateDiscountPolicy extends DiscountPolicy {
    private BigDecimal discountRate;

    public RateDiscountPolicy() {
        this.discountRate = BigDecimal.valueOf(0.1);
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    @Override
    public void applyDiscount(Order order) {
        BigDecimal price = order.getPrice();
        BigDecimal discountPrice = price.multiply(discountRate);
        order.setPrice(price.subtract(discountPrice));

        System.out.println("Rate Discount");
        System.out.println(price + " Minus " + discountPrice);
    }
}
