package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * (연관 정보)
 * xToOne 관계 (ManyToOne, OneToOne)
 * Order
 * Order → Member
 * Order → Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    // 무한루프에 빠진다. 해결하려면 Order 와 양방향이 걸려있는 곳에 전부 @JsonIgnore 을 추가해줘야 한다. 둘 중에 하나는 끊어줘야하기 때문.
    // 위 문제를 해결해도 지연로딩때문에 또 다른 문제가 생기는데, Hibernate5Module 라이브러리를 설치해서 지연로딩일 경우에는 JSON 라이브러리에게 아무것도 뿌리지 말라고 해서 해결해야 한다.
    // + API 상 필요없는 data 까지 sql 으로 다 조회되면서 성능 문제도 생긴다.
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;
    }
}
