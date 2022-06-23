import java.lang.reflect.Field;
import java.util.List;

public class TestUtil
{
  public static <T> void print(T t)
  {
    final StringBuffer sb = new StringBuffer();
    final Field [] fields = t.getClass().getDeclaredFields();

    for(Field field : fields)
    {
      field.setAccessible(true);

      try
      {
        isField(t.getClass());
        sb.append(field.getName());
        sb.append(" : ");
        sb.append(field.get(t));
        sb.append(",");
      }
      catch ( IllegalAccessException e )
      {
        e.printStackTrace();
      }

      field.setAccessible(false);
    }
//    System.out.println("t = " + t);
  }

  public static <T> void print(List<T> lists)
  {
    for(T t : lists)
    {
      print(t);
    }
  }

  private static boolean isField(Class<?> clazz)
  {
    System.out.println("clazz = " + clazz);
    System.out.println("clazz.getTypeName() = " + clazz.getTypeName());
    return true;
  }
}
