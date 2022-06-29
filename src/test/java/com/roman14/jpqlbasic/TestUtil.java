package com.roman14.jpqlbasic;

import java.lang.reflect.Field;
import java.util.List;

public class TestUtil
{
  public static <T> void print(T t)
  {
    final StringBuffer sb = new StringBuffer();
    final Field [] fields = t.getClass().getDeclaredFields();

    sb.append(t.getClass().getName());
    sb.append('\n');

    for(Field field : fields)
    {
      field.setAccessible(true);
      if ( isPrimary(field, "com.roman14") )
      {
        try
        {
          sb.append(field.getName());
          sb.append(" : ");
          sb.append(field.get(t));
          sb.append(",");
        }
        catch ( IllegalAccessException e )
        {
          e.printStackTrace();
        }
        sb.append('\n');
      }
      field.setAccessible(false);
    }
    System.out.println(sb.toString());
  }

  public static <T> void print(List<T> lists)
  {
    for(T t : lists)
    {
      print(t);
    }
  }

  /**
   *
   * @param field
   * @param baseLocation
   * @return
   */
  private static boolean isPrimary(Field field, String baseLocation)
  {
    return field.getType().toString().indexOf(baseLocation) < 0;
  }
}
