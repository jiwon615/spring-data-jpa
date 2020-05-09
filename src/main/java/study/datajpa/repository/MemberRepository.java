package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.Lob;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    /**
     * 메소드 이름으로 쿼리 생성
     */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);


    /**
     *  만약 위 메소드명에 조건이 훨씬 많이 점점 길어지면 한없이 길어지는 단점.. 그것을 극복위해 바로 JPQL 아래처럼 작성 가능
     */
    // 레포지토리 메소드에 쿼리 정의하기
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);  // 메소드명은 아무렇게나!

    // 값 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // DTO 조회
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    /**
     * 여러가지 쿼리 반환타입
     * (메소드 명 아무렇게나 지으면 됨 --> find...By  )
     */
    List<Member> findByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 optional

    /** 페이징 처리
     *
     */
    Page<Member> findByAge(int age, Pageable pageable);  // 반환타입을 Page, Slice, List 중 적절히 선택해서 쓰자

    /**
     * 벌크성 쿼리
     *
     */
    @Modifying(clearAutomatically = true)// .executeUpdate(); 를 실행해주는 어노테이션이므로 꼭 넣어주어야 함
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /** 1. 패치조인
     * 패치조인 사용해서 Member객체 select해올 때 Team객체 내용도 한번에 가져오도록 하는 방법
     *
     */
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    /**
     * 2. Enityty Graph  (원랜 fetch join해야 Team객체까지 join했지만, 이 방식으로 훨씬 간단하게 할 수 있음)
     *
     * @return
     */
    @Override
    @EntityGraph(attributePaths = "team")
    List<Member> findAll();

    /**
     * JPA SQL Hint
     *
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /**
     * JPA lock
     *
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
