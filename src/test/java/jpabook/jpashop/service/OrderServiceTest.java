package jpabook.jpashop.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class OrderServiceTest {
	
	@Autowired EntityManager em;
	@Autowired OrderService orderService;
	@Autowired OrderRepository orderRepository;
	
	private Member createMember() {
		Member member = new Member();
		member.setName("회원1");
		member.setAddress(new Address("인천","청마로","34번길"));
		em.persist(member);
		
		return member;
	}
	
	private Book createBook(String name, int orderPrice, int stockQuantity) {
		Book book = new Book();
		book.setName(name);
		book.setPrice(orderPrice);
		book.setStockQuantity(stockQuantity);
		em.persist(book);
		
		return book;
	}

	@Test
	public void 상품주문() throws Exception {
		//given
		Member member = createMember();
		Book book = createBook("JPA TEST", 10000, 10);
		
		int orderCount = 2;
		
		//when
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
		
		//then
		Order getOrder = orderRepository.findOne(orderId);
		
		assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
		assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
		assertEquals("주문 가격은 가격 * 수량이다.", 10000 * orderCount, getOrder.getTotalPrice());
		assertEquals("주문 수량만큼 재고가 줄어야 한다", 8, book.getStockQuantity());
	}
	
	@Test
	public void 주문취소() throws Exception {
		
		//given
		Member member = createMember();
		Book book = createBook("JPA TEST", 10000, 10);
		
		int orderCount = 2;

		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
		
		//when
		orderService.cancleOrder(orderId);
		
		//then
		Order getOrder = orderRepository.findOne(orderId);
		
		assertEquals("주문 취소시 상태는 CANCLE 이다", OrderStatus.CANCLE, getOrder.getStatus());
		assertEquals("주문 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, book.getStockQuantity());
		
	}
	
	@Test
	public void 상품주문_재고수량초과() throws Exception {
		
		//given
		Member member = createMember();
		Book book = createBook("JPA TEST", 10000, 10);
		
		int orderCount = 11;
		
		//when
		try {
			orderService.order(member.getId(), book.getId(), orderCount);
		} catch (NotEnoughStockException e) {
			return;
		}
		
		//then
		fail("재고 수량 부족 예외가 발생해야 한다.");
	}
}
