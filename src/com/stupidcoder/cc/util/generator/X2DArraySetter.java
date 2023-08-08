package com.stupidcoder.cc.util.generator;


import com.stupidcoder.cc.util.OpenRange;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class X2DArraySetter extends XWritable{
    private final String arrName;
    /**
     * key: (state << 32) | next, value: 输入的字符
     */
    private final Map<Long, OpenRange> repeatedSetters = new HashMap<>();
    /**
     * key：(startByte << 8) | endByte, value: 此范围下的赋值语句
     */
    private final Map<Integer, XWritableList> repeatedFor = new HashMap<>();

    /**
     * 存放最终得到的赋值语句
     */
    private final XWritableList result = new XWritableList();

    public X2DArraySetter(String arrName) {
        this.arrName = arrName;
    }

    /**
     * 对二维数组进行赋值操作，arr[state][input] = next
     * @param state 第一个参量
     * @param input 第二个参量
     * @param next 第三个参量
     * @return 自己
     */
    public X2DArraySetter set(int state, byte input, int next) {
        long key = ((long)state << 32) | next;
        OpenRange range = repeatedSetters.getOrDefault(key, OpenRange.empty());
        range.union(input);
        repeatedSetters.putIfAbsent(key, range);
        return this;
    }

    public void finish() {
        repeatedSetters.forEach((key, range) -> {
            int state = (int) (key >> 32);
            int next = (int) (key & 0xFFFFFFFFL);
            range.forEach((l, r) -> {
                if (r - l > 2) {
                    //超过一个元素
                    int k = (l << 8) | r;
                    var list = repeatedFor.getOrDefault(k, new XWritableList().disableFinalLineBreak());
                    list.append(arrName + "[%d][i] = %d;", state, next);
                    repeatedFor.putIfAbsent(k, list);
                } else {
                    //单个元素的赋值语句直接加进去
                    result.append(arrName + "[%d]['%c'] = %d;", state, l + 1, next);
                }
            });
        });
        repeatedSetters.clear();
        repeatedFor.forEach((key, list) -> {
            int start = (key >> 8) + 1, end = key & 0xFF;
            String expr1 = "int i = '" + Character.toString(start) + "'";
            String expr2 = "i <= '" + Character.toString(end - 1) + "'";
            String expr3 = "i ++";
            result.append(XFor.of(expr1, expr2, expr3).addContent(list));
        });
        repeatedFor.clear();
    }

    @Override
    protected void setIndentations(int i) {
        super.setIndentations(i);
        result.setIndentations(i);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        finish();
        result.output(writer);
    }
}
