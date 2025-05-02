package Service.Factory;

import Service.Policy.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class DiscountPolicyFactory {
    private final Map<String, Supplier<DiscountPolicy>> discountPolicyMap = new HashMap<>();

    public DiscountPolicyFactory() {
        discountPolicyMap.put(DiscountType.MEMBERSHIP.name(), MemberShipDiscountPolicy::new);
        discountPolicyMap.put(DiscountType.RATE.name(), RateDiscountPolicy::new);
        discountPolicyMap.put(DiscountType.MINUS.name(), MinusDiscountPolicy::new);
    }

    public DiscountPolicy createDiscountPolicyList(List<String> discountPolicyList) {
        List<DiscountPolicy> discountPolicies = discountPolicyList
                .stream().map(discountPolicyMap::get)
                .filter(Objects::nonNull)
                .map(Supplier::get)
                .toList();

        if(discountPolicies.isEmpty())
            return null;

        // 최초로 실행되는 할인 정책
        DiscountPolicy head = discountPolicies.getFirst();
        DiscountPolicy prev = head;

        // 체인 구성
        for(int i = 1; i < discountPolicies.size(); i++) {
            DiscountPolicy next = discountPolicies.get(i);
            prev.setNextPolicy(next);
            prev = next;
        }

        return head;
    }
}
