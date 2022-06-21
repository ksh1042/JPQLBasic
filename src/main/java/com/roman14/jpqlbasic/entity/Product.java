package com.roman14.jpqlbasic.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Product
{
  @Id @GeneratedValue
  @Column(name = "product_id", nullable = false, unique = true)
  private Long id;

  private String name;

  private int price;

  private int stockAmount;

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

  public int getPrice()
  {
    return price;
  }

  public void setPrice(int price)
  {
    this.price = price;
  }

  public int getStockAmount()
  {
    return stockAmount;
  }

  public void setStockAmount(int stockAmount)
  {
    this.stockAmount = stockAmount;
  }
}
