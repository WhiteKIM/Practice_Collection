package whitekim.practice.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whitekim.practice.common.exception.NotExistOrderedItemException;
import whitekim.practice.item.dto.request.RegisterItemForm;
import whitekim.practice.item.dto.response.RespItemInfo;
import whitekim.practice.item.entity.Item;
import whitekim.practice.item.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public RespItemInfo getItemInfo(Long itemId) {
        Optional<Item> optItem = itemRepository.findById(itemId);

        if(optItem.isEmpty()) {
            throw new RuntimeException();
        }

        return RespItemInfo.toDto(optItem.get());
    }

    public void registerItemInfo(RegisterItemForm itemForm) {
        Item entity = itemForm.toEntity();

        itemRepository.save(entity);
    }

    public BigDecimal calPurchasePrice(Long itemId, Long purchaseCount) {
        Optional<Item> optItem = itemRepository.findById(itemId);

        if(optItem.isEmpty()) {
            throw new NotExistOrderedItemException();
        }

        Item item = optItem.get();

        // 가격 반환
        return item.getPrice().multiply(BigDecimal.valueOf(purchaseCount));
    }

    public void manageInventory(Long itemId, Long purchaseCount) {
        Optional<Item> optItem = itemRepository.findById(itemId);

        if(optItem.isEmpty()) {
            throw new NotExistOrderedItemException();
        }

        Item item = optItem.get();
        item.manageInventory(purchaseCount);
    }
}
