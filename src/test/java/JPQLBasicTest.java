import com.roman14.jpqlbasic.JPQLBasic;
import com.roman14.jpqlbasic.entity.Member;
import com.roman14.jpqlbasic.entity.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class JPQLBasicTest
{
  private JPQLBasic subject;

  @BeforeEach
  void setUp()
  {
    subject = new JPQLBasic();
  }


  @Test
  void testAddMember()
  {
    final Team team = new Team();

    team.setName("back-end 01");

    final Member member = new Member();

    member.setName("Moon");
    member.setAge(28);
    member.setTeam(team);

    subject.addMember(member);
  }

  @Test
  void testFindMember()
  {
    final Team team = new Team();

    team.setName("back-end 01");

    final Member member = new Member();

    member.setName("Moon");
    member.setAge(28);
    member.setTeam(team);

    subject.addMember(member);

    final Long id = 1L;

    Optional<Member> findMember = subject.findMember(id);

    Assertions.assertTrue(
      findMember.isPresent()
    );

    Assertions.assertTrue(
      member.equals( findMember.get() )
    );
  }

  @AfterEach
  void tearDown()
  {
    subject.close();
  }
}