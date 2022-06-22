# JPQL Basic traning
- 김영한 - 자바 ORM 표준 JPA 프로그래밍


## 목표
- JPQL 사용법 습득


## 목차


1. [JPQL 기초문법](#1.-JPQL-기초-문법)
2. [JPQL Query 종류](#2.-JPQL-Query-종류)
3. [Query 결과 조회 방법](#3.-Query-결과-조회-방법)
4. [프로젝션](#4.-프로젝션)
5. [페이징 API](#5.-페이징-API)
6. [조인](#6.-조인)
7. [서브쿼리와 JPQL 서브쿼리의 한계](#7.-서브쿼리와-JPQL-서브쿼리의-한계)

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
- 별칭은 반드시 사용해야 한다. ( ```MMMMMM as m``` )
```jpaql
SELECT m FROM MMMMMM as m WHERE m.age > 18
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
em.createQuery("SELECT m.name, m.age FROM Member m", String.class);
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
SELECT t FROM Member m join m.team; // 명시적 조인
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
#### 8.4. 타입 캐스팅 비교
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

### 9. CASE
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