package whitekim.practice.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whitekim.practice.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
