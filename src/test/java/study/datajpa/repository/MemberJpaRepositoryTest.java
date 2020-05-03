package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

// @RunWith(SpringRunner.class)  spring boot + jUnit5 조합으로는 이거 생략함
@SpringBootTest
@Transactional  // 모든 jpa 변경은 transaction 안에서 이루어져야 하므로 이게 필요 (없으면 에러!)
@Rollback(false) // 이게 없으면 test 돌리고 table 모두 drop하므로 insert된 것을 h2에서 확인 불가
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());  // Assertions.assertThat() 일때 Assertions 위에 option(alt + enter)
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

}