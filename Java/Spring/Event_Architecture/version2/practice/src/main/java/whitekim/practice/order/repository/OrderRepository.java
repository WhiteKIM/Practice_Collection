package whitekim.practice.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whitekim.practice.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
