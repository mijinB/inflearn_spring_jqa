package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());     // MemberForm 을 같이 보내서 createMemberForm 화면에서 MemberForm 에 접근할 수 있다.
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {        // @Valid 를 붙이면 스프링에서 validation 을 해준다. (ex. MemberForm.java > @NotEmpty)  / @Valid 와 BindingResult 가 있으면 BindingResult 에 오류가 담겨서 create 메서드를 실행하게 된다.

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();     // 지금은 서버사이드라서 Entity 를 화면으로 보내주고 화면에서 필요한 Entity 필드들을 꺼내서 노출시켜주지만, API 를 사용하게 될 경우에는 절대로!! Entity 를 넘겨줘서는 안된다. ⇒ Form 객체나 DTO(Data Transfer Object)를 사용하고 Entity 는 최대한 순수하게 유지를 해야한다. / DTO(Data Transfer Object) : 계층 간 데이터 교환을 위해 사용되는 객체, 로직을 가지지 않는 데이터 객체, getter, setter 메소드만 가진 클래스를 의미한다.
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
