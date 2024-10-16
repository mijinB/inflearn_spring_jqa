package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 비즈니스 로직 (도메인 모델 패턴)
    // data(stockQuantity(재고 수량))를 가지고 있는 곳에서 비즈니스 로직이 나가는게 응집력이 좋다.
    // 엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용하는 것을 "도메인 모델 패턴"이라고 한다. ↔ 반대로, 엔티티에는 비즈니스 로직이 거의 없고 Service 계층에서 대부분의 비즈니스 로직을 처리하는 것을 "트랜잭션 스크립트 패턴"이라고 한다.

    /**
     * 재고 수량 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * 재고 수량 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
