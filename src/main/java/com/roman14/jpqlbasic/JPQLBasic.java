package com.roman14.jpqlbasic;

import com.roman14.jpqlbasic.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;

public class JPQLBasic
{
  private final EntityManagerFactory emf;
  private final EntityManager em;

  public JPQLBasic()
  {
    this.emf = Persistence.createEntityManagerFactory("JPQLBasic");
    this.em = this.emf.createEntityManager();
  }

  public <T> void add(T t)
  {
    em.getTransaction().begin();

    try
    {
      em.persist(t);
    }
    catch(Exception e)
    {
      if(em != null) em.getTransaction().rollback();
      e.printStackTrace();
      throw e;
    }

    em.getTransaction().commit();
  }

  public Optional<Member> findMember(Long id)
  {
    em.getTransaction().begin();
    Optional<Member> result = Optional.empty();

    try
    {
      result = Optional.of( em.find(Member.class, id) );
    }
    catch(Exception e)
    {
      if(em != null) em.getTransaction().rollback();
      e.printStackTrace();
      throw e;
    }

    em.getTransaction().commit();
    return result;
  }

  public <T> List<T> clearAndQuery(String query, Class<T> clazz)
  {
    em.getTransaction().begin();

    em.clear();

    List<T> result = em.createQuery(query, clazz).getResultList();

    em.getTransaction().commit();

    return result;
  }

  public void close()
  {
    if( em != null ) em.close();
    if( emf != null ) emf.close();
  }
}
