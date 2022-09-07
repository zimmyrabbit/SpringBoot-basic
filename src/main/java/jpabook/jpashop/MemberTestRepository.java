package jpabook.jpashop;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class MemberTestRepository {

	@PersistenceContext
	private EntityManager em;
	
	public Long save(MemberTest member) {
		em.persist(member);
		return member.getId();
	}
	
	public MemberTest find(Long id) {
		return em.find(MemberTest.class, id);
	}
}
