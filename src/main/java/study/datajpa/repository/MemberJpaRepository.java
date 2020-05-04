package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);

        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    // 예전 jpa 에서는 findOne 이었지만, 최신에는 findById 이렇게 바뀜
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);  // member가 Null일 수도 아닐 수도 있기에 Optional type으로!!
        return Optional.ofNullable(member);
    }

    public long count() {  // count 함수의 반환타입이 long 이므로
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();  // 단건조회
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    /** 페이징 처리
     *
     */
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                        .setParameter("age", age)
                        .setFirstResult(offset)
                        .setMaxResults(limit)
                        .getResultList();
    }

    // 페이징 처리 할 전체 갯수
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }
}
