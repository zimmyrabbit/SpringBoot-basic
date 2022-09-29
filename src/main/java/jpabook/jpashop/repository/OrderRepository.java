package jpabook.jpashop.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

	private final EntityManager em;
	
	public void save(Order order) {
		em.persist(order);
	}
	
	public Order findOne(Long id) {
		return em.find(Order.class, id);
	}
	
	public List<Order> findAllByString(OrderSearch orderSearch) {
		
		String jpql = "select o from Order o join o.member m";
		boolean isFirstCondition = true;
		
		//주문 상태 검색
		if(orderSearch.getOrderStatus() != null) {
			if(isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			}else {
				jpql += " and";
			}
			jpql += " o.status = :status";
		}
		
		//회원 이름 검색
		if(StringUtils.hasText(orderSearch.getMemberName())) {
			if(isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			}else {
				jpql += " and";
			}
			jpql += " m.name like :name";
		}
		
		TypedQuery<Order> query = em.createQuery(jpql, Order.class)
				.setMaxResults(1000);
		
		if(orderSearch.getOrderStatus() != null) {
			query = query.setParameter("status", orderSearch.getOrderStatus());
		}
		if(StringUtils.hasText(orderSearch.getMemberName())) {
			query = query.setParameter("name", orderSearch.getMemberName());
		}
		
		return query.getResultList();
	}
	
	//JPA Criteria
	public List<Order> findAllByCriteria(OrderSearch orderSearch) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Order> cq = cb.createQuery(Order.class);
		Root<Order> o = cq.from(Order.class);
		Join<Object, Object> m = o.join("member", JoinType.INNER);
		
		List<Predicate> creteria = new ArrayList<>();
		
		//주문 상태 검색
		if(orderSearch.getOrderStatus() != null) {
			Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
			creteria.add(status);
		}
		
		if(StringUtils.hasText(orderSearch.getMemberName())) {
			Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
			creteria.add(name);
		}
		
		cq.where(cb.and(creteria.toArray(new Predicate[creteria.size()])));
		TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
		
		return query.getResultList();
	}
	
	public List<Order> findAll(OrderSearch orderSearch) {
		return em.createQuery("select o from Order o join o.member m"
						+ "where o.status = :status"
						+ "and m.name like :name", Order.class)
						.setParameter("status", orderSearch.getOrderStatus())
						.setParameter("name", orderSearch.getMemberName())
						.setMaxResults(1000)
						.getResultList();
	}

	public List<Order> findAllWithMemberDelivery() {
		
		return em.createQuery("select o from Order o"
							+ " join fetch o.member m"
							+ " join fetch o.delivery d", Order.class)
							.getResultList();
	}

	public List<Order> findAllWithItem() {
		
		// JPA distinct
		// -> 조회해오는 루트 엔티티(order) 의 레퍼런스 객체 주소가 같으면 중복으로 판단, 제거함.
		
		//** 1대다 페치조인에서는 페이징이 불가능 하다.
		//** 컬렉션 페치조인은 1개만 사용 가능 (order <-> orderItem)
		
		return em.createQuery("select distinct o from Order o"
							+ " join fetch o.member m"
							+ " join fetch o.delivery d"
							+ " join fetch o.orderItems oi"
							+ " join fetch oi.item i", Order.class)
							.getResultList();
	}

	public List<Order> findAllWithMemberDelivery(int offset, int limit) {
		return em.createQuery("select o from Order o"
				+ " join fetch o.member m"
				+ " join fetch o.delivery d", Order.class)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}
}
