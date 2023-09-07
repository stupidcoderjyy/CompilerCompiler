package stupidcoder.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

public class ArrayUtil {

    public static <T> void resize(Collection<T> c, int size, Supplier<T> defaultValue) {
        for (int i = c.size() ; i < size ; i ++) {
            c.add(defaultValue.get());
        }
    }

    public static <T> void resize(Collection<T> c, int size) {
        for (int i = c.size() ; i < size ; i ++) {
            c.add(null);
        }
    }

    public static <T> T[] resize(T[] arr, int size, Supplier<T> defaultValue) {
        T[] result = Arrays.copyOf(arr, size);
        for (int i = arr.length - 1 ; i < size ; i ++) {
            result[i] = defaultValue.get();
        }
        return result;
    }

    public static int[] resize(int[] arr, int size, int defaultVal) {
        int[] result = Arrays.copyOf(arr, size);
        Arrays.fill(result, arr.length, size - 1, defaultVal);
        return result;
    }
}
