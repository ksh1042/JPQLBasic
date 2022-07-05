# JPQL Basic traning
- 김영한 - 자바 ORM 표준 JPA 프로그래밍


## 목표
- JPQL 사용법 습득


## 목차


1. [JPQL 기초문법](#1-JPQL-기초-문법)
2. [JPQL Query 종류](#2-JPQL-Query-종류)
3. [Query 결과 조회 방법](#3-Query-결과-조회-방법)
4. [프로젝션](#4-프로젝션)
5. [페이징 API](#5-페이징-API)
6. [조인](#6-조인)
7. [서브쿼리와 JPQL 서브쿼리의 한계](#7-서브쿼리와-JPQL-서브쿼리의-한계)
8. [타입 캐스팅 비교](#8-타입-캐스팅-비교)
9. [CASE와 NULL 조회](#9-CASE와-NULL-조회)
10. [함수](#10-함수)
11. [경로 표현식](#11-경로-표현식)
12. [Fetch](#12-Fetch)
13. [다형성 쿼리](#13-다형성-쿼리)
14. [Named](#14-Named)
15. [벌크 연산](#17-벌크-연산)

### 1. JPQL 기초 문법

- 기존 SQL의 ```FROM```에 들어가는 테이블에서 **엔티티 이름**을 사용한다. (테이블 이름이나 클래스 이름이 아니므로 주의)
```java
@Entity(name = "MMMMMM")  // 해당 name을 사용한다.
@Table(name = "Member")
public class Member
{
  ...
}
```
- 별칭은 반드시 사용해야 한다. ( ```MMMMMM as m``` or ```MMMMMM m``` )
```jpaql
SELECT m FROM MMMMMM as m WHERE m.age > 18
```
```jpaql
SELECT m FROM MMMMMM m WHERE m.age > 18
```
<br>

### 2. JPQL Query 종류



#### 2.1. TypedQuery
- 쿼리로 반환되는 타입이 명확할 경우에 사용한다.
```java
TypedQuery<Member> tq = em.createQuery("SELECT m FROM Member m", Member.class);
```
#### 2.2. Query
- 반환 타입이 명확하지 않을 경우에 사용한다.
```java
Query q = em.createQuery("SELECT r FROM Robot r");
```
```java
// 명확한 타입을 반환할 경우 (String, Integer)
em.createQuery("SELECT m.name FROM Member m", String.class);
em.createQuery("SELECT m.age FROM Member m", Integer.class);

// 정체 모를 타입을 반환할 경우 (Query)
em.createQuery("SELECT m.name, m.age FROM Member m");
```

#### 2.3. 파라미터 바인딩
- ```:parameter``` 해당 키워드로 파라미터를 받는다. (기존에 사용하던 SQL Mapper와 유사함)
```java
em.createQuery("SELECT m FROM Member m WHERE m.name = :name", Member.class)
  .setParameter("name", value)
  .getResultList();
```
- ```?1``` 해당 키워드를 통해 인덱스 기준으로 파라미터를 받을수도 있다.
```java
em.createQuery("SELECT m FROM Member m WHERE m.name = ?1", Member.class)
  .setParameter(1, value)
  .getResultList();
```
<br>

### 3. Query 결과 조회 방법



#### 3.1. 다수의 객체 조회
- ```query.getResultList()```를 사용한다.
```java
List<Member> members = query("SELECT m Member m").getResultList();
```


#### 3.2. 단일 객체 조회
- ```query.getSingleResult()```를 사용한다.
```java
Member members = query("SELECT m Member m WHERE m.id = 1L").getSingleResult();
```


#### 3.2.1. 단일 객체 조회 예외처리
- ```NoResultException``` : 단일 객체 조회시 결과가 없을 경우 해당 예외가 발생한다.
```java
try
{
  Member findMember = em.createQuery("SELECT m FROM Member m WHERE m.name = 'KIM'").getSingleResult();
}
catch(NoResultException e)
{
// TODO -> 결과가 없을 경우의 코드를 작성
}
```
- ```NonUniqueResultException``` : 단일 객체 조회 시도에 결과가 둘 이상일 경우 해당 예외가 발생한다.
```java
try
{
  Member findMember = em.createQuery("SELECT m FROM Member m WHERE m.name = 'KIM'").getSingleResult();
}
catch(NonUniqueResultException e)
{
  // TODO -> 결과가 둘 이상 조회되었을 경우의 코드를 작성
}
```
<br>

### 4. 프로젝션



#### 4.1. 엔티티 프로젝션 1
```jpaql
SELECT m FROM Member m
```
#### 4.2. 엔티티 프로젝션 2
- ```join```이 발생한다.
```java
@Entity
public class Team {
  private List<Member> members = new ArrayList<>();
}

@Entity
public class Member {
  @ManyToOne
  private Team team;
}
```
```jpaql
SELECT m.team FROM Member m;        // 묵시적 조인
SELECT t FROM Member m join m.team t; // 명시적 조인
```
#### 4.3. 임베디드 타입 프로젝션
```java
@Embedable
public class Address {
}
@Entity
public class Member {
  @Embedded
  private Address address;
}
```
```jpaql
SELECT m.address FROM Member m
```
#### 4.4. 스칼라 타입 프로젝션
- 결과는 Object 배열로 가져온다.
```
```jpaql
SELECT DISTINCT m.name, m.age FROM Member m
```
- ```new``` 예약어를 통해 별도의 DTO 생성자를 통한 조회가 가능하다.
- 생성자 사용 시 패키지 경로까지 정확히 명시를 해주어야 한다.
- 반드시 조회하고자 하는 쿼리의 파라미터 타입과 갯수가 맞는 생성자가 존재해야 한다.
```java
package com.roman14.entity;
public class ResultDTO
{
  private String name;
  private int age;
  
  public ResultDTO(String name, int age)
  {
    this.name = name;
    this.age = age;
  }
}
```
```jpaql
SELECT NEW com.roman14.entity.ResultDTO( m.name, m.age ) FROM Member m
```
<br>

### 5. 페이징 API



- JPA는 추상 API로 페이징을 제공한다.
- 두 메소드를 통해서 편리하게 페이징이 가능하다. (```setFirstResult()``` ```setMaxResult()```)
```java
em.createQuery("SELECT m FROM Member m ORDER BY m.age ASC", Member.class)
  .setFirstResult(0)
  .setMaxResult(10)
  .getResultList();
```
<br>

### 6. 조인
#### 6.1. 내부 조인
- INNER JOIN
- A (AB) B
```jpaql
SELECT m FROM Member m JOIN m.team
```
#### 6.2. 외부 조인
- OUTER [LEFT | RIGHT] JOIN
- LEFT : (A AB) B
```jpaql
SELECT m FROM Member m LEFT JOIN m.team
```
#### 6.3. 세타 조인
- 임의의 연관관계를 지정하여 조인하는 방식
- 내부적으로 어느정도 최적화가 된 쿼리를 반환한다.
```jpaql
SELECT m FROM Member m, Order o, m.addTime = o.orderDate
```
#### 6.4 ON 조인
- JOIN 대상 필터링
```jpaql
SELECT m FROM Member m LEFT JOIN m.team t ON t.name = 'back-end 01'
```
```sql
/* native sql 1 */
SELECT m.*
FROM Member m
,    Team t
WHERE m.team_id = t.id
ANd   t.name = 'back-end 01'
```
```sql
/* native sql 2 */
SELECT m.*
FROM Member m
    LEFT JOIN Team t
ON  m.team_id = t.id
ANd t.name = 'back-end 01'
```
- 별도로 연관관계를 지정하지 않은 엔티티, 별도의 필드로 외부조인이 가능하다. (Hibernate 5.1 ↑)
```jpaql
SELECT m FROM Member m LEFT JOIN m.team t ON m.name = t.name
```

### 7. 서브쿼리와 JPQL 서브쿼리의 한계
- JPA 표준스펙에서의 서브쿼리는 WHERE, HAVING 에서만 사용이 가능하다.
- ※Hibernate 구현체에서는 예외적으로 SELECT 구간에 서브쿼리를 생성할 수 있도록 지원해준다.
#### 7.1. exist, ALL, ANY, SOME
- exist
```jpaql
/* 'Front-End 01' 팀 이름을 가진 소속의 멤버들  */
SELECT m FROM Member m WHERE m.team = EXISTS(
    SELECT t FROM Team t WHERE t.name = 'Front-End 01' 
) 
```
- ALL
```jpaql
/* 전체 상품 중 재고보다 주문 수량이 더 많은 주문들 */
SELECT o FROM Orders o WHERE o.orderAmount > ALL(
    SELECT p.stockAmount FROM Product p
)  
```
- ANY, SOME
```jpaql
/* 소속된 팀이 있는 멤버 */
SELECT m FROM Member m WHERE m.team = SOME(
    SELECT t FROM Team t
)```
#### 7.2. FROM 구간의 서브쿼리가 필요한 경우
>- 쿼리를 두 번으로 쪼개서 실행한다.
>- 서브쿼리에 필요한 동작을 애플리케이션으로 가져와서 처리 한다.
>- JPQL이 아닌 네이티브 쿼리를 사용한다.

### 8. JPQL 타입 표현
- 대부분 표준 ANSI SQL과 비슷하다.
#### 8.1. 숫자 타입
- 10L(Long), 20.22D(Double), 10.5F(Float)
#### 8.2. Enum 타입
- 사용 시 패키지 경로를 반드시 포함하여야 한다.
```java
package com.roman14.jpqlbasic.entity;

public enum City {
  SEOUL, DAEGEON, DAEGU, BUSAN;
}

@Entity
public class Member  {
  @Enumerated(EnumType.STRING)
  private City city;
}
```
```jpaql
SELECT m From Member m WHERE m.city = com.roman14.jpqlbasic.entity.City.SEOUL
```
- 보통은 위처럼 번거롭게 패키지명을 전부 기술하지 않고, 파라미터 바인딩을 통해 처리하는 방법도 있다.
### 8. 타입 캐스팅 비교
- 상속관계에 있는 엔티티를 비교할때에 사용한다.
- ```@Discrimination``` 어노테이션을 통해 상속관계의 구분자가 있는 경우에만 사용 가능하다.
- ```WHERE TYPE(Entity_alias) = Entity```
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Discrimination
public class Product {}

@Entity
@DiscriminationValue("G")
public class Guitar extends Product  {}
```
```jpaql
/* 타입이 Guitar인 상품만 조회 */
SELECT p FROM Product p WHERE TYPE(p) = Guitar 
```

### 9. CASE와 NULL 조회
#### 9.1. 일반 CASE
```jpaql
SELECT 
    CASE WHEN m.age <= 19 THEN '청소년'
    CASE WHEN m.age >= 65 THEN '노인'
         ELSE '성인' 
    END 
FROM Member m
```
#### 9.2. 단순 CASE
```jpaql
SELECT 
    CASE p.dtype
        WHEN 'G' THEN '기타'
        WHEN 'P' THEN '피아노'
        ELSE 'NAN'
    END 
FROM Product p
```
#### 9.3. COALESCE
- 오라클의 NVL과 같이 NULL일 경우에 대체값을 지정할 수 있다. NULL이 아닌 경우엔 그냥 값을 반환한다.
```jpaql
/* 사용자 이름이 NULL일 경우 '관리자' 를 반환하고, 그 외의 경우엔 그냥 값을 반환 */
SELECT COALESCE(m.name, '관리자') FROM Member m
```
#### 9.4. NULLIF
- 두 인자 값이 일치할 경우 NULL을 반환하고, 그 외의 경우엔 그냥 값을 반환한다.
```jpaql
/* 44세는 NULL 값으로 반환되며, 그 외에는 자신의 값을 반환 */
SELECT NULLIF(m.age, 44) FROM Member m
```

### 10. 함수
#### 10.1 기본 함수
- JPQL에서 기본적으로 지원하는 함수들로 해당 함수들은 데이터베이스 Dialect와 상관없이 사용이 가능하다.
- ```CONCAT```, ```SUBSTRING```, ```TRIM```, ```LOWER```, ```UPPER```, ```LENGTH```, ```LOCATE```, ```ABS```, ```SQRT```, ```MOD```, ```SIZE```, ```INDEX```
> ```size``` : 컬렉션의 사이즈 값을 구하하는 함수
> ```jpaql
> /* 팀에 소속된 멤버 수 */
> SELECT SIZE(t.members) FROM Team t
> ```
#### 10.2. 사용자 정의 함수
- 데이터베이스 Dialect를 지정하면 기본적으로 Hibernate에서 지원하는 Diarect의 경우 함수들이 대부분 등록되어 있다.
- 사용자가 직접 정의한 데이터베이스의 함수는 다음과 같은 과정을 거쳐 사용할 수 있다.
1. 데이터베이스 Dialet를 상속 받는다.
2. 상속받은 클래스의 생성자에 ```registerFunction()``` 함수를 통해 사용자 정의 함수를 등록한다.
```java
public class CustomOracleDialect extends Oracle12cDialect {
  public CustomOracleDialect() {
    // 두 번째 인자의 생성자는 외우고 사용하기는 어려우니 직접 슈퍼클래스의 코드를 조회해서 비슷한 기능의 사례를 참고해서 사용하는 편이 좋다. 
    this.registerFunction("fn_get_custom_stockAmount", new StandardSQLFunction("fn_get_custom_stockAmount", StandardBasicTypes.STRING));
  }
}
```
```jpaql
SELECT FUNCTION('fn_get_custom_stockAmount', p.stockAmount) FROM Product p
```
```jpaql 
```

### 11. 경로 표현식
- 상태 필드(state field) : 단순 값을 저장하기 위한 필드
- 연관 필드(association field) : 엔티티 혹은 엔티티 컬렉션 연관관계를 위한 필드
```jpaql
SELECT m.name   /* 상태 필드(값)  */
,      m.team   /* 연관 필드(엔티티) @ManyToOne, @OneToOne  */
,      m.orders /* 연관 필드(엔티티 컬랙션) @OneToMany, @ManyToMany  */
FROM Member m
```
- 연관 필드 조회 시 암묵적인 ```JOIN```이 발생하여 성능저하를 일으킬 수 있다.
- 가급적 암묵적 ```JOIN```이 아닌 명시적 ```JOIN```으로 표현하여야 유지보수에 추가적인 어려움을 겪지 않을수 있다.

### 12. Fetch 

#### 12.1.```N+1 문제```
> - ```FETCH JOIN``` 없이 조인하여 조회하게 될 경우 최약의 경우, 조인해야 될 대상의 N개 만큼 조회 쿼리가 추가적으로 발생할 수 있다.
>
>```java
>Team t1 = new Team("t1");
>Team t2 = new Team("t2");
>
>Member m1 = new Member(t1)
>Member m2 = new Member(t1);
>Member m3 = new Member(t2);
>
>...
>
>List<Member> members = em.createQueryt("SELECT m FROM Member m", Member.class).getResultList();
>
>for(Member m : members)
>{
>  // m2의 경우는 이미 앞서 m1에서 조회되어 영속성 컨텍스트에 존재하는 팀을 사용하므로, 팀1와 팀2 총 두번의 조회 쿼리가 발생한다. 
>  m.getTeam().getName();
>}
>```
>- 즉, 처음 조회해오는 ```select m FROM Member m``` 전체 멤버 쿼리 한 번과, 그 뒤에 조회될 ```select t from team where t.id=?``` 팀 N개의 갯수만큼 조회. ```N+1```문제가 발생하는 것이다.
#### 12.2. Fetch Join
- JPQL에서의 ```FETCH JOIN```이란 쪼개서 가져오는 것이 아닌 동시에 즉, 한 번에 가져올 것을 명시하는 방법
```jpaql
/* 실행한 JPQL */
SELECT m FROM Member m JOIN FETCH m.team t
```
```sql
/* 예상 SQL 실행 쿼리 */
SELECT m.*, t.*
FROM MEMBER m INNER JOIN TEAM t ON m.team_id = t.id
```
- ```FETCH JOIN```을 통해 조회된 내부 엔티티는 프록시 엔티티가 아닌 실제 엔티티가 영속성 컨텍스트에 담기게 된다.
- ```@ManyToOne(fetch = FetchType.LAZY``` 보다 우선순위로 실행된다.
#### 12.3. 1:N 조회 데이터 중복 문제 
- ```@OneToMany```의 경우 즉, 컬렉션 패치 조인에는 데이터가 중복조회되는 현상이 발생한다.
> ```jpaql
> SELECT t FROM Team t JOIN FETCH t.members m
> ```
> ```sql
> /* 오라클의 해당 SQL와 같은 방식으로 실행된다 */
> SELECT * FROM Member m, Team t
> ```
> ```
> /* 아래와 같이 조회된다 */
> row1 = | team 1 | member 1 |
> row2 = | team 1 | member 2 |
> row3 = | team 2 | member 3 |
> row4 = | team 2 | member 4 |
> row5 = | team 2 | member 5 |
>```
>- JPQL에 ```DISTINCT```를 추가하여 중복을 제거 가능하다.
> ```jpaql
>   SELECT DISTINCT t FROM TEAM t JOIN t.members
> ```
>- JPQL의 ```DISTINCT```는 일반적인 SQL의 ```DISTINCT```명령도 수행하며, 추가적으로 애플리케이션 내에서 중복의 결과를 제거해주는 두 가지 역할을 수행한다.

#### 12.4. 일반 Join과 Fetch Join의 차이
- 일반 Join의 경우는 대상 엔티티만 조회하고 필드 엔티티는 조회해오지 않는다.
- Fetch Join의 경우는 조회 대상 엔티티의 필드 엔티티를 함께 조회하여 영속성 컨텍스트에 올린다.


#### 12.5. Fetch Join 사용 시 주의점
- Fetch Join은 별칭을 주어 그래프 탐색을 실행할 경우 좋지 않은 결과를 가져올 수 있다.
```jpaql
SELECT t FROM Team as t JOIN FETCH t.members as m WHERE m.age > 10
```
- N:1 관계는 문제가 없으나, 1:N 관계에서 세 개 이상의 컬렉션을 Fetch Join 할 경우 데이터 정합성에 문제가 생길 수가 있다.
> 두 개의 컬렉션을 패치 조인 할 경우에도 N+1 문제가 발생하는데 그 이상의 경우 N x M x ...+1 문제가 발생할 수 있기 때문
- 컬렉션 Fetch Join을 실행하면 페이징 API 사용이 불가능하다. (Hibernate의 경우는 경고메세지를 출력하고 앱 메모리 내에서 페이징 연산을 수행한다)
>- setFirstResult
>- setMaxResult 

#### 12.6. @BatchSize
- N:1 관계에서 N+1 문제를 해결하기 위해 사용되는 차선책이다.
- Fetch Join 실행 시 ```WHERE IN``` 조건을 생성하여 한 번에 조회해온다.
- 보통 1~1000 값을 준다. 
```java
public class Team {
  @BatchSize(size = 100)
  @OneToMany
  private List<Member> members = new ArrayList<>();
}
```
- persistence.xml을 통해 글로벌 설정으로 사용이 가능하다.
```xml
<properties>
  ...
  <property name="hibernate.default_batch_fetch_size" value="100" />
</properties>
```

### 13. 다형성 쿼리
#### 13.1. TYPE
- ```type``` 키워드를 통해 특정 서브클래스 엔티티만 조회가 가능하다.
- 해당 슈퍼클래스 엔티티에 ```@DiscriminatorColumn```를 명시하여 구분자를 추가해야 사용 가능하다.
```jpaql
/* JPQL */
SELECT m FROM Member as m WHERE TYPE(m) NOT IN (Admin, Regular)  
```
```sql
/* SQL */
SELECT m.* FROM TB_MEMBER as m WHERE m.DTYPE IN ('A', 'R')
```

#### 13.2. TREAT
- 자바의 타입캐스팅과 유사. 슈퍼클래스 엔티티에서 서브클래스 엔티티로 캐스팅이 필요할 경우에 사용된다.
- SELECT, FROM, WHERE 에서 사용이 가능하다.
```jpaql
/* 트랙 수가 5개를 넘는 앨범을 아이템으로 조회 */
SELECT i FROM Item as i WHERE TREAT(i as Album).totalTrack > 5
```

### 14. Named
- 자주 사용하는 쿼리를 어노테이션을 통해 별도의 이름으로 등록이 가능하다.
```java
@Entity
@NamedQuery(name = "Member.findById", query = "SELECT m FROM Member as m WHERE m.userName = :userName")
public class Member {}
```
```java
em.createNamedQuery("Member.findById", Member.class)
  .setParameter("userName", "문동훈")
  .getResultList();
```
- 어플리케이션 실행 중 해당 쿼리의 검증을 마치므로 런타임중 문법오류가 발생하지 않는다.
- xml을 통해 매핑도 가능하다.
```xml
<persistence-unit name = "JpaBasic">
  <mapping-file>META-INF/named/Member.xml</mapping-file>
  <properties>
    ...
  </properties>
</persistence-unit>
```
```xml
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" version="2.2">
  <named-query name="Member.findByName">
    <![CDATA[
        SELECT m
        FROM Member as m 
        WHERE m.userName = :userName
    ]]>
  </named-query>
</entity-mappings>
```
- Spring JPA Repository 인터페이스의 ```@Query```는 해당 기능을 이용해 구현되어있다.
```java
public interface MemberRepository extends JpaReopsotiry<Member, Long> {
  /**
   * 사용자 이름과 나이를 통해 사용자들을 조회한다.
   * @param name  사용자 이름
   * @param age   사용자 나이
   * @return
   */
  @Query("SELECT m FROM Member as m WHERE m.name = ?1 AND m.age = ?2")
  private List<Member> findByNameAndAge(String name, int age);
}
```

### 15. 벌크 연산
- JPA는 기본적으로 실시간, 객체 단위를 중시하여 대규모 트랜잭션에는 취약하다.
- 모든 회원의 구독 기간을 한 달 추가하고자 할 때에 현재 회원의 숫자 만큼 N번의 UPDATE 쿼리가 발생할 수도 있다. 
- 대량의 UPDATE, DELETE 동작을 수행할때 사용하는 것 (전체 직원의 연봉 10% 인상 같은 쿼리 등)
- 일반적인 SQL의 업데이트문과 같이 별도로 정의하여 실행한다.
```java
// 실행되어 업데이트 된 행의 수를 반환 
int resultCnt = em.createQuery("UPDATE Employee as e SET e.salary = e.salary +  e.salary * 0.1 WHERE e.level = :level ")
  .setParameter("level", employeeLevel)
  .executeUpdate();
em.clear(); // ,영속성
```
- Hibernate를 사용할 경우 ```INSERT INTO ... SELECT```와 같은 SELECT INSERT도 지원한다.
> 영속성 컨텍스트에서 관리되는 수행 단위가 아니므로, 주의해서 사용하여야 한다. 기본적인 해결방법은 아래와 같다.
> 1. 벌크 연산을 먼저 수행하여 영속성 컨텍스트와의 불일치성을 사전에 막는다.
> 2. 벌크 연산 후 계속하여 해당 엔티티를 영속성 컨테스트에서 관리하고자 하면 flaush, clear 동작을 수행하여 수정된 엔티티를 새로 조회해 불일치성을 막아야 한다.