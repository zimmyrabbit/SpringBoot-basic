package jpabook.jpashop.service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class initDb {
	
	private final InitService initService;
	
	@PostConstruct
	public void init() {
		initService.dbInit1();
		initService.dbInit2();
	}

	@Component
	@RequiredArgsConstructor
	@Transactional
	static class InitService {
		private final EntityManager em;
		
		private Member createMember(String name, String city, String street, String zipCode) {
			Member member = new Member();
			member.setName(name);
			member.setAddress(new Address(city,street,zipCode));
			return member;
		}
		
		private Book createBook(String bookName, int price, int stockQuantity) {
			Book book = new Book();
			book.setName(bookName);
			book.setPrice(price);
			book.setStockQuantity(stockQuantity);
			return book;
		}
		
		private Delivery createDelivery(Member member) {
			Delivery delivery = new Delivery();
			delivery.setAddress(member.getAddress());
			return delivery;
		}
		
		public void dbInit1() {
			Member member = createMember("UserA", "서울", "종로", "11");
			em.persist(member);
			
			Book book1 = createBook("JPA1 BOOK",10000,100);
			em.persist(book1);
			
			Book book2 = createBook("JPA2 BOOK",20000,200);
			em.persist(book2);
			
			OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
			OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
			
			Delivery delivery = createDelivery(member);
			Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
			em.persist(order);
		}
		
		public void dbInit2() {
			Member member = createMember("UserB", "인천", "검단", "22");
			em.persist(member);
			
			Book book1 = createBook("Spring1 BOOK",20000,200);
			em.persist(book1);
			
			Book book2 = createBook("Spring2 BOOK",40000,300);
			em.persist(book2);
			
			OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
			OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);
			
			Delivery delivery = createDelivery(member);
			Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
			em.persist(order);
			
		}
	}
	
}
