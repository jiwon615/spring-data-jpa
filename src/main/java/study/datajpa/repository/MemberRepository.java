package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

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
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 optional
}
