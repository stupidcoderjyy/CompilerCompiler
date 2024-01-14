package stupidcoder.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ArrayUtil {

    public static <T> void resize(List<T> c, int size, Supplier<T> defaultValue) {
        if (c instanceof ArrayList<T> l) {
            l.ensureCapacity(size);
        }
        for (int i = c.size() ; i < size ; i ++) {
            c.add(defaultValue.get());
        }
    }

    public static <T> void resize(List<T> c, int size) {
        if (c instanceof ArrayList<T> l) {
            l.ensureCapacity(size);
        }
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
