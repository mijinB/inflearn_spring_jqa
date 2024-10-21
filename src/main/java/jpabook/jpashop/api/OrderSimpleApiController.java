package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // ORDER 2개 (N = 2)
        // 1 + N ⇒ 1 + MEMBER N + Delivery N = 5
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        // 패치 조인을 이용해서 쿼리가 한 번만 실행된다.
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();     // LAZY 초기화 ⇒ memberId 를 통해 영속성 컨텍스트에서 찾아보고 없으면 DB 쿼리를 날리는 것
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();     // LAZY 초기화
        }
    }

}
