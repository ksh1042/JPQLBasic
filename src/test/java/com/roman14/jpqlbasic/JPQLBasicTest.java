package com.roman14.jpqlbasic;

import com.roman14.jpqlbasic.entity.Member;
import com.roman14.jpqlbasic.entity.Product;
import com.roman14.jpqlbasic.entity.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.*;

class JPQLBasicTest
{
  private JPQLBasic subject;

  @BeforeEach
  void setUp()
  {
    subject = new JPQLBasic();
  }


  @Test
  void testAdd()
  {
    final Team team = new Team();
    team.setName("back-end 01");

    final Member member = new Member();

    member.setName("Moon");
    member.setAge(28);
    member.setTeam(team);

    Assertions.assertDoesNotThrow(
      () -> subject.add(team)
    );
    Assertions.assertDoesNotThrow(
      () -> subject.add(member)
    );
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

    subject.add(member);

    final Long id = 1L;

    Optional<Member> findMember = subject.findMember(id);

    Assertions.assertTrue(findMember.isPresent());

    Assertions.assertTrue(member.equals(findMember.get()));
  }

  @Test
  void testClearAndQuery()
  {
    setTestMembers(20);

//    final String query = "SELECT m FROM Member m JOIN FETCH m.team t WHERE t.name = 'Front-End 01'";
    final String query = "SELECT m FROM Member m JOIN FETCH m.team t";
    final List<Member> members = subject.clearAndQuery(query, Member.class);

    for(Member m : members)
    {
      System.out.println("m.getTeam().getClass() = " + m.getTeam().getClass());
      System.out.println("m.getTeam().getName() = " + m.getTeam().getName());
    }
  }

  @AfterEach
  void tearDown()
  {
    subject.close();
  }

  private void setTestMembers(int amount)
  {
    final SecureRandom sr = new SecureRandom();
    final List<Team> teams = new ArrayList<>();
    final String [] teamNames = {"Back-End 01", "Back-End 02", "Front-End 01", "Front-End 02", "Marketing", "Planning"};

    for(int i=0; i<teamNames.length; i++)
    {
      final Team team = new Team();
      team.setName( teamNames[i] );
      teams.add(team);
      subject.add(team);
    }
    for(int i=0; i<amount; i++)
    {
      final Member member = new Member();
      member.setName( getPersonName() );
      member.setAge( sr.nextInt(18) + 10 );
      member.setTeam(teams.get( sr.nextInt(5) ));
      subject.add(member);
    }
  }

  private void setTestProduct()
  {
    final SecureRandom sr = new SecureRandom();
    final String [] productNames = {"얼음", "철광석", "규소", "황", "인", "황산", "구리", "주석", "금", "은", "텅스텐", "니비듐", "물", "증기", "석탄", "석유", "중유", "경유", "메탄", "플라스틱", "소금", "우라늄"};

    for(int i=0; i<productNames.length; i++)
    {
      final Product product = new Product();
      product.setName(productNames[i]);
      product.setPrice( sr.nextInt(200_000) + 1_000 );
      product.setStockAmount( sr.nextInt(20_000) );

      subject.add(product);
    }
  }

  private static String getPersonName()
  {
    List<Character> lastNames = Arrays.asList('김', '이', '박', '최', '정', '강', '조', '윤', '장', '임', '한', '오', '서', '신', '권', '황', '안', '송', '류', '전', '홍', '고', '문', '양', '손', '배', '조', '백', '허', '유', '남', '심', '노', '정', '하', '곽', '성', '차', '주', '우', '구', '신', '임', '나', '전', '민', '유', '진', '지', '엄', '채', '원', '천', '방', '공', '강', '현', '함', '변', '염', '양', '변', '여', '추', '노', '도', '소', '신', '석', '선', '설', '마', '길', '주', '연', '방', '위', '표', '명', '기', '반', '왕', '금', '옥', '육', '인', '맹', '제', '모', '장', '남', '탁', '국', '여', '진', '어', '은', '편', '구', '용');
    List<Character> firstNames = Arrays.asList('가', '강', '건', '경', '고', '관', '광', '구', '규', '근', '기', '길', '나', '남', '노', '누', '다', '단', '달', '담', '대', '덕', '도', '동', '두', '라', '래', '로', '루', '리', '마', '만', '명', '무', '문', '미', '민', '바', '박', '백', '범', '별', '병', '보', '빛', '사', '산', '상', '새', '서', '석', '선', '설', '섭', '성', '세', '소', '솔', '수', '숙', '순', '숭', '슬', '승', '시', '신', '아', '안', '애', '엄', '여', '연', '영', '예', '오', '옥', '완', '요', '용', '우', '원', '월', '위', '유', '윤', '율', '으', '은', '의', '이', '익', '인', '일', '잎', '자', '잔', '장', '재', '전', '정', '제', '조', '종', '주', '준', '중', '지', '진', '찬', '창', '채', '천', '철', '초', '춘', '충', '치', '탐', '태', '택', '판', '하', '한', '해', '혁', '현', '형', '혜', '호', '홍', '화', '환', '회', '효', '훈', '휘', '희', '운', '모', '배', '부', '림', '봉', '혼', '황', '량', '린', '을', '비', '솜', '공', '면', '탁', '온', '디', '항', '후', '려', '균', '묵', '송', '욱', '휴', '언', '령', '섬', '들', '견', '추', '걸', '삼', '열', '웅', '분', '변', '양', '출', '타', '흥', '겸', '곤', '번', '식', '란', '더', '손', '술', '훔', '반', '빈', '실', '직', '흠','흔', '악', '람', '뜸', '권', '복', '심', '헌', '엽', '학', '개', '롱', '평', '늘', '늬', '랑', '얀', '향', '울', '련');

    Collections.shuffle(lastNames);
    Collections.shuffle(firstNames);

    return "" + lastNames.get(0) + firstNames.get(0) + firstNames.get(1);
  }
}