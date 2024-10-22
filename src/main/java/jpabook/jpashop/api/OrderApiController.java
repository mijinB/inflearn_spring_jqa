package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            // 강제 초기화 ⇒ 실제로 해당 값이 필요할 때 추가적인 데이터베이스 쿼리가 발생하지 않도록 하기 위해 사용(미리 로드)
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        return all;
    }

    // 쿼리가 너무 많이 실행된다.
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // 패치 조인으로 SQL 이 한번만 실행된다.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /* ⭐
    * ToOne(OneToOne, ManyToOne) 관계를 모두 페치조인 한다. ⇒ ToOne 관계는 row 수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다.
    * 컬렉션은 지연 로딩으로 조회한다.
    * 💡application.yml 파일에 default_batch_fetch_size: 100(size 만큼 IN 쿼리로 조회) 를 추가함으로써 orderItem 을 2개씩 총 4번(order 2개) 쿼리를 실행하던게 한번의 쿼리로 된다. ⇒ 100개를 한번에 조회. 엄청 빠르기때문에 성능 최적화 도움을 준다.
    *   (100이기 때문에 쿼리가 1000개이면 10번 돌겠지만 또 size 를 1000으로 수정한다면 1:1이 되는 것이다.) + 100~1000 사이를 선택하는 것을 권장.애매하면 100~500.
    *  ⇒ v3에서는 쿼리 1개로 됐지만, 페이징처리 불가능! & 정규화되기 전의 결과. 중복o / v3.1은 쿼리는 3번이지만, 페이징처리가 가능! & 정규화 후의 결과. 중복x(쿼리 호출 수가 약간 증가하지만, DB 데이터 전송량이 감소한다.)
    * 결론 : ToOne 관계는 페치 조인해도 페이징에 영향을 주지 않는다. 따라서 ToOne 관계는 페치조인으로 쿼리 수를 줄이고 해결하고, 나머지(컬렉션)는 hibernate.default_batch_fetch_size(+@BatchSize) 로 최적화 하자. */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
            ) {

        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /* ToOne 관계들을 먼저 조회하고, ToMany(1:N) 관계는 각각 별도로 처리한다.
    *   ⇒ ToOne 관계는 조인해도 데이터 row 수가 증가하지 않지만,
    *     ToMany 관계는 조인하면 row 수가 증가하기 때문이다. */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {

        return orderQueryRepository.findOrderQueryDtos();
    }
    
    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            // DTO 안에 Entity 가 있으면 안된다. 완전히 Entity 의존을 없애야한다. ⇒ OrderItemDto 따로 생성해서 사용
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }


}
