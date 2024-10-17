package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        // id 값이 없다는 것은 새로 생성한 객체라는 뜻이기 때문에 없으면 persist 로 신규등록, 있으면 이미 DB 에 등록됐거나 어디서 가져온 것이기 때문에 merge 를 통해 update 개념으로 동작.(진짜 update 는 아니다.)
        if (item.getId() == null) {
            em.persist(item);
        } else {
            // ⭐변경 감지와 병합(merge) 의 차이를 완벽하게 이해해야 한다. / service > ItemService.java (변경 감지 기능 사용 예시) / !!주의 : 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만, 병합을 사용하면 모든 속성이 변경된다. ⇒ null 로 업데이트 할 위험이 있다.
            // 💡엔티티를 변경할 때는 항상 변경 감지를 사용해라.
            em.merge(item);     // 병합(merge) 사용도 변경 감지 기능과 동일한 역할을 한다. ⇒ 파라미터로 넘어온 준영속 Entity 의 식별자(id)로 찾아서 모든 걸 "바꿔치기" 하는 것. (=변경 감지 기능 사용처럼 한땀한땀 작성하는 코드를 JPA 가 한 줄의 코드로 해주는 것이다.)
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
