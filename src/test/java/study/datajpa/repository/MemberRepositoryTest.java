package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        // 있을 수도 없을 수도 있으므로 Optional로 제공
        Optional<Member> byId = memberRepository.findById(savedMember.getId());
        Member findMember = byId.get();
        // Member findMember = memberRepository.findById(savedMember.getId()).get();  위 2줄을 이러헤  1줄로 가능 !!

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 변경은 dirty checking 을 통해
        // findMember1.setUsername("memberName!!!!!!!!");


        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }
    
    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member member1 = new Member("AAA", 20);
        Member member2 = new Member("AAA", 10);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQueryFindUser() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void testQueryFindUsernameList() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> result = memberRepository.findUsernameList();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void testQueryFindMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team);
        memberRepository.save(member1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void testQueryFindByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }

    /**
     * 쿼리 반환타입 test
     */
    @Test
    public void returnType() {
        Member member1 = new Member("AAA", 20);
        Member member2 = new Member("BBB", 10);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 1. 컬렉션
//        List<Member> findMember = memberRepository.findListByUsername("AAA");
//        System.out.println("findMember: " + findMember.size());

        // 2. 단건
//        Member findMember2 = memberRepository.findMemberByUsername("AAA");
//        System.out.println("findMember2: " + findMember2);

        // 3. 단건 optional
        Optional<Member> findMember3 = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findMember3: " + findMember3.get());
    }

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // Page가 알아서 totalCount는 가져오니 매우 편리함 (반환타입: Page, Slice, List 중 알맞게 선택해서 쓰자)
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // Member를 그대로 반환하면 안되고, 아래처럼 DTO로 변환하여 반환할 것
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //        for (Member member : content) {
//            System.out.println("member: " + member);
//        }
//        System.out.println("totalElements" + totalElements);

        //then
        List<Member> content = page.getContent(); // 내용 꺼내오기

        assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수   (Slice 에는 없는 기능)
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호     (Slice 에는 없는 기능)
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush();  // 벌크성 쿼리 나린 뒤엔 가급적 em.flush와 em.clear 해줄 것
//        em.clear();


        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 : " + member5);

        // then
        assertThat(resultCount).isEqualTo(3);
    }


    @Test
    public void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findAll(); // @EntityGraph 사용한 방식
      //  List<Member> members = memberRepository.findMemberFetchJoin();  // 패치조인

        for (Member member : members) {
            System.out.println("member: " + member.getUsername());  // member1
            System.out.println("member.teamClass: " + member.getTeam().getClass());  // fetch join도 EntityGraph도 없을 땐 가짜객체(HibernagPorxy)로 가져오겠지만, 이제는 진짜 객체이름이 잘 나옴
            System.out.println("member.team: " + member.getTeam().getName()); // teamA
        }
    }

    /**
     * JPA Query Hint
     *
     */
    @Test
    public void queryHint() {
        // given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    /**
     * JPA Lock
     *
     */
    @Test
    public void lock() {
        // given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    /**
     * 사용자 정의 인터페이스 속 메소드 호출
     *
     */
    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();

    }
}