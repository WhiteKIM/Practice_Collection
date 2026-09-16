package whitekim.practice.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whitekim.practice.common.exception.NotExistItemException;
import whitekim.practice.common.exception.NotExistOrderedItemException;
import whitekim.practice.item.dto.request.RegisterItemForm;
import whitekim.practice.item.dto.response.RespItemInfo;
import whitekim.practice.item.entity.Item;
import whitekim.practice.item.repository.ItemRepository;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public RespItemInfo getItemInfo(Long itemId) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(NotExistItemException::new);

        return RespItemInfo.toDto(item);
    }

    public Long registerItemInfo(RegisterItemForm itemForm) {
        Item entity = itemForm.toEntity();

        Item saveItem = itemRepository.save(entity);

        return saveItem.getId();
    }

    public BigDecimal calPurchasePrice(Long itemId, Long purchaseCount) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(NotExistOrderedItemException::new);

        // 가격 반환
        return item.getPrice().multiply(BigDecimal.valueOf(purchaseCount));
    }

    /**
     *
     * @param itemId
     * @param purchaseCount
     */
    public void manageInventory(Long itemId, Long purchaseCount) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(NotExistOrderedItemException::new);

        item.manageInventory(purchaseCount);

//        @NOTE 나중에 이벤트로 바뀌면 그때 사용 고려
//        if(ItemManageType.ROLLBACK.equals(type)) {
//            item.rollbackItemStock(purchaseCount);
//        } else {
//            item.manageInventory(purchaseCount);
//        }
    }
}
