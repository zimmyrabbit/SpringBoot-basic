package jpabook.jpashop.api;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

//@Controller + @ResponseBody = @RestController
@RestController
@RequiredArgsConstructor
public class MemberApiController {

	private final MemberService memberService;
	
	//파라미터로 Entity를 직접 사용하는 방식 (bad)
	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);
		
		return new CreateMemberResponse(id);
	}
	
	//파라미터로 api전용 dto를 만들어 사용하는 방식 (good)
	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
		
		Member member = new Member();
		member.setName(request.getName());
		
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}
	
	
	@Data
	static class CreateMemberRequest {
		@NotEmpty
		private String name;
	}
	
	@Data
	static class CreateMemberResponse {
		private Long id;
		
		public CreateMemberResponse(Long id) {
			this.id = id;
		}
	}
}
