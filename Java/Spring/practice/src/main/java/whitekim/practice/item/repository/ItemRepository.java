package whitekim.practice.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whitekim.practice.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
