package com.roman14.jpqlbasic.entity;

import com.roman14.jpqlbasic.entity.embedded.Address;

import javax.persistence.*;

@Entity
@Table(name = "Orders")
public class Order
{
  @Id @GeneratedValue
  @Column(name = "order_id", nullable = false, unique = true)
  private Long id;

  private int orderAmount;

  @Embedded
  private Address address;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public int getOrderAmount()
  {
    return orderAmount;
  }

  public void setOrderAmount(int orderAmount)
  {
    this.orderAmount = orderAmount;
  }

  public Address getAddress()
  {
    return address;
  }

  public void setAddress(Address address)
  {
    this.address = address;
  }

  public Product getProduct()
  {
    return product;
  }

  public void setProduct(Product product)
  {
    this.product = product;
  }

  public Member getMember()
  {
    return member;
  }

  public void setMember(Member member)
  {
    this.member = member;
  }
}
