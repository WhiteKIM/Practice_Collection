package Service;

import Model.Item;
import Model.Order;
import Model.User;
import Service.Factory.DiscountPolicyFactory;
import Service.Policy.DiscountPolicy;
import Service.Policy.DiscountType;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final DiscountPolicyFactory discountPolicyFactory = new DiscountPolicyFactory();

    public void test() {
        User user = new User();
        Item item1 = new Item("test1", 10000);
        Item item2 = new Item("test1", 20000);

        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);

        List<String> discountPolicyList = new ArrayList<>();
        discountPolicyList.add(DiscountType.RATE.name());
        discountPolicyList.add(DiscountType.MINUS.name());
        discountPolicyList.add(DiscountType.MEMBERSHIP.name());

        DiscountPolicy discountPolicy = discountPolicyFactory.createDiscountPolicyList(discountPolicyList);

        Order order = new Order(itemList, user, discountPolicy);
        System.out.println(order.getPrice());
    }
}
