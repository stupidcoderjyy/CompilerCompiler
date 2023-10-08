package stupidcoder.util;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static int getIntField(Object obj, String name) {
        try {
            return getField(obj, name).getInt(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getObjectField(Object obj, String name) {
        try {
            return (T) getField(obj, name).get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
