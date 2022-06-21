# JPQL Basic traning
- 김영한 - 자바 ORM 표준 JPA 프로그래밍

---

## 목표
- JPQL 사용법 습득

---

## 목차

---
1. [JPQL 기초문법](#1.-JPQL-기초-문법)
2. [JPQL Query 종류](#2.-JPQL-Query-종류)
3. [Query 결과 조회 방법](#3.-Query-결과-조회-방법)
4. [프로젝션](#4.-프로젝션)
5. [페이징 API](#5.-페이징-API)
6. [조인](#6.-조인)

### 1. JPQL 기초 문법

---
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

---

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

---

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

---

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

---

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

---

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