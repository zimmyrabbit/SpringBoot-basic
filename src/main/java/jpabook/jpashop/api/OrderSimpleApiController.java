package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/*
 * 
 * Order				xToOne
 * Order -> Member		(ManyToOne)
 * Order -> Delivery	(OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

	private final OrderRepository orderRepository;
	
	@GetMapping("/api/v1/simple-orders")
	public List<Order> orderV1() {
		
		List<Order> all =orderRepository.findAllByString(new OrderSearch());
		for(Order order : all) {
			order.getMember().getName(); //Lazy 로딩 강제 초기화
			order.getDelivery().getAddress(); //Lazy 로딩 강제 초기화
		}
		
		return all;
	}
	
	@GetMapping("/api/v2/simple-orders")
	public List<SimpleOrderDto> orderV2() {
		// ORDER 2개 조회
		// N + 1 -> 1 + Member N + Delivery N
		//	-> Lazy로딩이 초기화 될 때 영속성context에 값이 없으면 쿼리를 쏜다.
		//	-> 해결법 : fetch join 최적화
		List<Order> order = orderRepository.findAllByString(new OrderSearch());
		
		List<SimpleOrderDto> list = order.stream()
										.map(o -> new SimpleOrderDto(o))
										.collect(Collectors.toList());
		
		return list;
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
			name = order.getMember().getName(); //Lazy 로딩 초기화
			orderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			address = order.getDelivery().getAddress(); //Lazy 로딩 초기화
		}
		
	}
}
