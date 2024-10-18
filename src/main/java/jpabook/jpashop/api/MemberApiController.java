package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController     // @Controller 와 @ResponseBody 가 하나에 포함되어 있다.
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /*
    * v1의 경우, Member Entity 에서 name 속성을 username 이라고 변경하게 되면 Front 에서 api 호출 시, NotEmpty 애러를 만나게 된다. & Entity 에 @NotEmpty 를 사용해서 모든 API 에서 필수 값이 된다.
    * v2의 경우, 속성 이름을 변경하면 saveMemberV2 메서드에서 컴파일 오류가 나기때문에 API 배포 전 오류를 잡을 수 있다는 것이 장점이다. ⇒ API 스펙이 달라지지 않는다. & API 마다 @NotEmpty 사용할 수도 사용 안할 수도 있다.
    * ⭐API 는 등록, 요청 모두 절대 Entity 를 사용하지 말자. DTO 사용 */
    
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // 요청 DTO / API 요청 스펙에 맞추어 별도의 DTO 를 파라미터로 받는다.
    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    // 등록 DTO
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}
