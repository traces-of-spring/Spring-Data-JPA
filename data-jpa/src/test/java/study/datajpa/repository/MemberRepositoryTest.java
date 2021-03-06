package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDTO;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("멤버 테스트")
    void testMember() throws Exception {
        // given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        // when
        Optional<Member> byId = memberRepository.findById(savedMember.getId());
        Member findMember = byId.get();

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
        
    }

    @Test
    @DisplayName("기본적인 CRUD 테스트")
    void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    @DisplayName("이름과 나이로 찾는 테스트")
    void findByUsrenameAndAgeGreaterThanTest() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("find Top3 test")
    void findTop3() throws Exception {
        // given
        List<Member> top3 = memberRepository.findTop3By();
        // when

        // then

    }

//    @Test
//    @DisplayName("이름으로 찾는 테스트")
//    void findByUsernameTest() throws Exception {
//        // given
//        Member member1 = new Member("AAA", 10);
//        Member member2 = new Member("BBB", 20);
//        memberRepository.save(member1);
//        memberRepository.save(member2);
//
//        // when
//        List<Member> result = memberRepository.findByUsername("AAA");
//
//        // then
//        assertThat(result.get(0)).isEqualTo(member1);
//    }

    @Test
    @DisplayName("@Query 테스트")
    void testQuery() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findUser("AAA", 10);

        // then
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    @DisplayName("@Query 테스트")
    void findUsernameList() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<String> result = memberRepository.findUsernameList();

        // then
        for (String s : result) {
            System.out.println(" s = " + s);
        }
    }

    @Test
    @DisplayName("@Query DTO 테스트")
    void findMemberDTOtest() throws Exception {
        // given
        Team team = new Team("team1");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team);
        memberRepository.save(member1);

        // when
        List<MemberDTO> result = memberRepository.findMemberDTO();

        // then
        for (MemberDTO s : result) {
            System.out.println(" s = " + s);
        }
    }

    @Test
    @DisplayName("@Query - 컬렉션 파라미터 바인딩 테스트")
    void findByNames() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        // then
        for (Member m : result) {
            System.out.println(" Member = " + m);
        }
    }

    @Test
    @DisplayName("반환 타입 테스트")
    void findOptional() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Optional<Member> result = memberRepository.findOptionalByUsername("BBB");

        // then
        System.out.println("result = " + result);
    }


    @Test
    @DisplayName("순수 JPA 페이징과 정렬 테스트")
    void paging() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        memberRepository.save(new Member("member7", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        int offset = 0;
        int limit = 3;

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDTO> map = page.map(member -> new MemberDTO(member.getId(), member.getUsername(), null));

        for (MemberDTO memberDTO : map) {
            System.out.println("memberDTO = " + memberDTO);
        }

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }

        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(7);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("bulkUpdate 테스트")
    void bulkUpdate() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear();

        // then
        assertThat(resultCount).isEqualTo(3);

    }
    
    @Test
    @DisplayName("지연 로딩 테스트")
    void findMemberLazy() throws Exception {
        // given
        // member1 -> teamA
        // member2 -> teamB

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
//        List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.team.getClass = " + member.getTeam().getClass());
            System.out.println("member.team.name = " + member.getTeam().getName());
        }

        // then
        
    }

    @Test
    @DisplayName("@EntityGraph 테스트")
    void entityGraphTest() throws Exception {
        // given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
//        List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
        }

        // then

    }

}