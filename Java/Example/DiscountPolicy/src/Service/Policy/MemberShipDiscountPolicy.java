package Service.Policy;

import Model.MemberShip;
import Model.Order;
import Model.User;

import java.math.BigDecimal;

public class MemberShipDiscountPolicy extends DiscountPolicy {
    private MemberShip memberShip;

    public void setMemberShip(MemberShip memberShip) {
        this.memberShip = memberShip;
    }

    @Override
    public void applyDiscount(Order order) {
        User buyer = order.getBuyer();
        this.memberShip = buyer.getMemberShip();

        BigDecimal price = order.getPrice();
        BigDecimal discountRate = BigDecimal.valueOf(memberShip.getDiscountRate());
        BigDecimal discountPrice = price.multiply(discountRate);

        System.out.println("MemberShip Discount");
        System.out.println(price + " Minus " + discountPrice);

        order.setPrice(price.subtract(discountPrice));
    }
}
