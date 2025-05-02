package Model;

import java.math.BigDecimal;

public enum MemberShip {
    BRONZE(0.1),
    SILVER(0.2),
    GOLD(0.3);

    private final double discountRate;

    MemberShip(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return discountRate;
    }
}
