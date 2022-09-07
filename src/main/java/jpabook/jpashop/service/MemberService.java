package jpabook.jpashop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	
	//회원 가입
	@Transactional
	public Long join(Member member) {
		
		//중복 회원 검증
		validateDuplicateMember(member);
		
		memberRepository.save(member);
		
		return member.getId();
	}

	private void validateDuplicateMember(Member member) {
		List<Member> findMembers = memberRepository.findByName(member.getName());
		
		if(!findMembers.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		}
		
	}
	
	//회원 전체 조회
	
	public List<Member> findMembers() {
		List<Member> members = memberRepository.findAll();
		
		return members;
 	}
	
	public Member findOne(Long memberId) {
		Member member = memberRepository.findOne(memberId);
		
		return member;
	}
}
