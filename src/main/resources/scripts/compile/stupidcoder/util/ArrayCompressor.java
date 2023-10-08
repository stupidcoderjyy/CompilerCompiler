package stupidcoder.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayCompressor {
    private final ICompressedArraySetter setter;
    private List<List<String>> map = new ArrayList<>();
    private List<Integer> leftBorders = new ArrayList<>();
    private List<Integer> rightBorders = new ArrayList<>();
    private int size = 0;

    public ArrayCompressor(ICompressedArraySetter setter) {
        this.setter = setter;
    }

    public static int next(int arg1, int arg2, int[][] arr) {
        if (arg1 < 0 || arg1 >= arr[1].length || arr[1][arg1] < 0) {
            return 0;
        }
        int limit = arg1 == arr[1].length - 1 ? arr[0].length : arr[1][arg1 + 1];
        int o = arg2 - arr[2][arg1];
        if (o < 0) {
            return 0;
        }
        int pos = arr[1][arg1] + o;
        return pos < limit ? arr[0][pos] : 0;
    }

    public void set(int arg1, int arg2, String val) {
        ensureSize(arg1, arg2);
        map.get(arg1).set(arg2, val);
        int preLeft = leftBorders.get(arg1);
        int preRight = rightBorders.get(arg1);
        if (preLeft < 0) {
            leftBorders.set(arg1, arg2);
            rightBorders.set(arg1, arg2);
            size++;
        } else {
            int postLeft = Math.min(preLeft, arg2);
            int postRight = Math.max(preRight, arg2);
            size -= preRight - preLeft;
            size += postRight - postLeft;
            leftBorders.set(arg1, postLeft);
            rightBorders.set(arg1, postRight);
        }
    }

    private void ensureSize(int arg1, int arg2) {
        ArrayUtil.resize(map, arg1 + 1, ArrayList::new);
        ArrayUtil.resize(map.get(arg1), arg2 + 1, () -> null);
        ArrayUtil.resize(leftBorders, arg1 + 1, () -> -1);
        ArrayUtil.resize(rightBorders, arg1 + 1, () -> -1);
    }

    public void finish() {
        int rows = leftBorders.size();
        int pos = 0;
        setter.setSize(size, rows, rows);
        for (int r = 0 ; r < rows ; r ++) {
            setter.setStart(r, pos);
            setter.setOffset(r, leftBorders.get(r));
            if (leftBorders.get(r) < 0) {
                continue;
            }
            for (int c = leftBorders.get(r); c <= rightBorders.get(r) ; c++) {
                setter.setData(pos++, map.get(r).get(c));
            }
        }
        leftBorders = null;
        rightBorders = null;
        map = null;
    }
}
