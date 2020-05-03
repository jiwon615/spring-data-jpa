package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 만약 위 메소드명에 조건이 훨씬 많이 점점 길어지면 한없이 길어지는 단점.. 그것을 극복위해 바로 JPQL 아래처럼 작성 가능
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);  // 메소드명은 아무렇게나!

    // 값 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // DTO 조회
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();
}
