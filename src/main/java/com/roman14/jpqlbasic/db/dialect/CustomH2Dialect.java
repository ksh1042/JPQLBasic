package com.roman14.jpqlbasic.db.dialect;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StringType;

public class CustomH2Dialect extends H2Dialect
{
  public CustomH2Dialect()
  {
    super();
    this.registerFunction("group_concat", new StandardSQLFunction("group_concat", new StringType()));
  }
}
