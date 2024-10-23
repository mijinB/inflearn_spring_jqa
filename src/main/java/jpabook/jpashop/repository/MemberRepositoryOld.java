package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor        // @PersistenceContext 대신 [생성자 주입]을 사용할 수 있고, 이 또한 @RequiredArgsConstructor 를 사용하면 자동으로 만들어주기 때문에 간략하고 일관성 있다. (+ em에 final 추가)  / 현재는 스프링 데이터 JPA 가 없으면 생성자 주입으로 대신할 수 없지만, 향후에는 스프링 자체에서도 가능하도록 해줄 가능성이 있다.
public class MemberRepositoryOld {

    /* [생성자 주입] 사용으로 변경
    JPA 에서 제공하는 PersistenceContext 를 사용하면 스프링이 엔티티 매니저를 만들어서 em 이라는 변수에 주입해주게 된다.
    @PersistenceContext */
    private final EntityManager em;

    // JPA 가 저장하는 로직 (DB 에 insert 쿼리가 날라가는 것)
    public void save(Member member) {
        em.persist(member);
    }

    // Member 를 찾아서 반환해주는 로직
    public Member findOne(Long id) {
        // em.find(Type, PK)
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        // em.createQuery(JPQL, Type)
        return em.createQuery("select m from Member m", Member.class).getResultList();
        /* JPQL 과 SQL 은 문법은 조금 다르지만 기능적으로는 거의 동일하다.
        * SQL : TABLE 을 대상으로 쿼리를 한다.
        * JPQL : Entity(객체)를 대상으로 쿼리를 한다. */
    }

    public List<Member> findByName(String name) {
        // .setParameter 를 사용하는 이유는 동적으로 쿼리 파라미터 값을 지정하여 유연한 쿼리 실행을 가능하게 하기 위해서이다.
        // .getResultList 는 파라미터 값을 설정한 후에 쿼리를 실행하고, 그 결과를 리스트 형태로 반환한다.
        return em.createQuery("select m from Member m where m.name = :name", Member.class).setParameter("name", name).getResultList();
    }
}
