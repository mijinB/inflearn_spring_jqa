package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    // 변경 감지에 의해서 data 를 변경하는 방법 (이게 보통 더 나은 방법이다. ⇒ 이걸 JPA 가 한 줄의 코드로 해주는 것이 merge(병합) 하지만 주의점이 있다. repository > ItemRepository.java 참고)
    // public void updateItem(Long itemId, UpdateItemDto.itemDto) {  이런식으로 DTO 를 만들어서 하는 게 가장 좋다.
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);     // 영속 상태이다.
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
        // @Transactional 을 통해 Commit 이 되고, JPA 는 flush(영속성 컨텍스트에서 변경된 것이 어떤건지 다 찾는다.) 를 날려서 변경 사항을 감지하고 update 를 실행한다.
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
