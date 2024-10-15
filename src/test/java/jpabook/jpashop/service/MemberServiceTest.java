package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest 가 있어야 스프링 부트에 올려서 테스트를 진행할 수 있다.
@SpringBootTest
@Transactional      // data 변경을 위해 (있어야 ROLLBACK 이 가능하다.)
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    // @Rollback(false)     Transactional 은 기본적으로 Rollback 을 해주기 때문에(Test 에서만) @Rollback(false)를 해주지않으면, console 에서 insert 문을 볼 수 없다. / Rollback 을 false 로 해주고 DB 에 data 가 잘 들어갔는지 한번 더 확인해 볼수 있다.
    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long saveId = memberService.join(member);

        // then
        em.flush();     // Rollback 은 하되, console 에서 insert 문을 보고 싶다면 추가해주면 된다. 트랜잭션을 커밋할 때 자동으로 플러시가 발생하기 때문에 실무에서는 사용x
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);

        // then
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));
    }
}