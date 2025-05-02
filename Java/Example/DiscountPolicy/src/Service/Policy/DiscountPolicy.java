package Service.Policy;

import Model.Order;

public abstract class DiscountPolicy {
    private DiscountPolicy nextPolicy;

    public abstract void applyDiscount(Order order);
    public void setNextPolicy(DiscountPolicy nextPolicy) {
        this.nextPolicy = nextPolicy;
    }

    public void handle(Order order) {
        applyDiscount(order);

        if(nextPolicy == null) {
            return;
        }

        nextPolicy.handle(order);
    }

}
