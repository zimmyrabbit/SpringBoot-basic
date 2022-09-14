package jpabook.jpashop.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
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
}
