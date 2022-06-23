package com.roman14.jpqlbasic.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Member
{
  @Id @GeneratedValue
  @Column(name = "member_id", nullable = false, unique = true)
  private Long id;

  private String name;

  private int age;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  private Team team;

  public Team getTeam()
  {
    return team;
  }

  public void setTeam(Team team)
  {
    this.team = team;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getAge()
  {
    return age;
  }

  public void setAge(int age)
  {
    this.age = age;
  }

  @Override
  public boolean equals(Object o)
  {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    Member member = (Member) o;
    return getAge() == member.getAge() && Objects.equals(getId(), member.getId()) && Objects.equals(getName(), member.getName()) && Objects.equals(getTeam(), member.getTeam());
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(getId(), getName(), getAge(), getTeam());
  }
}
