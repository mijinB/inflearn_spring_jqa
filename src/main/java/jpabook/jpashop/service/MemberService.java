package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)     // JPA 의 모든 data 변경이나 로직들은 가급적이면 트랜잭션 안에서 실행되어야 하기 때문에 추가해줘야 한다. *readOnly 를 통해 조회를 더 최적화 할 수 있다. (이렇게 클래스에 주게되면, 수정되어야 하는 메서드에는 @Transactional 따로 넣어줘야 한다. 따로 넣는 게 우선권.)
@RequiredArgsConstructor            // [생성자 주입] 생략 가능. final 로 되어 있는 필드만 생성자로 만들어준다.
public class MemberService {

    private final MemberRepository memberRepository;

    /* [생성자 주입]
    @Autowired 생성자 주입을 가장 많이 쓴다. 생성자가 하나일 경우 생략 가능 *생성자 주입일 경우 변수에 final 권장.
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    } */

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 중복 회원 가입 불가 유효성 검사
     * (실무에서는 이렇게 메서드로 검증하는 것보다 DB 에서 Unique 로 설정해주는 것이 안전하고 좋다.)
     */
    private void validateDuplicateMember(Member member) {
        // EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            // IllegalStateException : 대상 객체의 상태가 호출된 메서드를 수행하기에 적절하지 않을 때 발생시킬 수 있는 예외 (대표적인 표준 예외)
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 한명 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
