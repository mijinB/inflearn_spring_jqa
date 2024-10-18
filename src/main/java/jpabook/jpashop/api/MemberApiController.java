package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController     // @Controller 와 @ResponseBody 가 하나에 포함되어 있다.
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /*
    * v1의 경우, Member 의 모든 속성 값이 다 노출된다. 특정 속성을 노출시키지 않으려면 Member.java Entity 에서 해당 속성에 @JsonIgnore 이라는 어노테이션을 추가해야된다.
    * v2의 경우, 노출하고 싶은 것만 DTO 로 생성해서 노출할 수 있다. */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    // object 로( { data: [] } 로 한번 감싸주기 위해 Result 필요
    // count 추가하고 memberV2 메서드에 collect.size() 하면 count, data 두가지를 반환해준다.
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    /*
    * v1의 경우, Member Entity 에서 name 속성을 username 이라고 변경하게 되면 Front 에서 api 호출 시, NotEmpty 애러를 만나게 된다. & Entity 에 @NotEmpty 를 사용해서 모든 API 에서 필수 값이 된다.
    * v2의 경우, 속성 이름을 변경하면 saveMemberV2 메서드에서 컴파일 오류가 나기때문에 API 배포 전 오류를 잡을 수 있다는 것이 장점이다. ⇒ API 스펙이 달라지지 않는다. & API 마다 @NotEmpty 사용할 수도 사용 안할 수도 있다.
    * ⭐(추천x 강제) API 는 등록, 요청 모두 절대 Entity 를 사용하지 말자. DTO 사용 */
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

    @PutMapping("api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
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
