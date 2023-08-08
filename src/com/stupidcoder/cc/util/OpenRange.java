package com.stupidcoder.cc.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 表达区间 (l1, r1) U (l2, r2) U ... U (ln, rn)，子区间之间相互无交集，且l1 < r1 <= l2 < r2 <= ... <= ln < rn
 */
public class OpenRange {
    private List<Integer> pairsLeft = new ArrayList<>(4);
    private List<Integer> pairsRight = new ArrayList<>(4);

    private int offset = 0;
    
    private OpenRange(int i1, int i2) {
        //intervals.add(Pair.of(i1, i2));
        pairsLeft.add(i1);
        pairsRight.add(i2);
    }

    private OpenRange() {
        this(0,0);
    }

    /**
     * 创建一个开区间
     * @param left 左端点
     * @param right 右端点，不能小于左端点
     * @return 开区间对象
     */
    public static OpenRange of(int left, int right) {
        if (left > right) {
            throw new IllegalArgumentException("Illegal Interval");
        }
        return new OpenRange(left, right);
    }

    /**
     * 创建一个空集
     * @return 空集对象
     */
    public static OpenRange empty() {
        return new OpenRange();
    }

    /**
     * 将开区间整体移动一段距离
     * @param offset 移动距离，小于0时左移
     * @return 自己
     */
    public OpenRange move(int offset) {
        this.offset += offset;
        return this;
    }

    public int leftBorder() {
        if (isEmpty()) {
            return Integer.MIN_VALUE;
        }
        return pairsLeft.get(0);
    }

    public int rightBorder() {
        if (isEmpty()) {
            return Integer.MIN_VALUE;
        }
        return pairsRight.get(pairsRight.size() - 1);
    }

    public void clear() {
        offset = 0;
        pairsLeft.clear();
        pairsRight.clear();
    }

    /**
     * 获得一个区间和自己的并集
     * @param left 区间左端点
     * @param right 区间右端点
     * @return 得到的并集
     */
    public OpenRange union(int left, int right) {
        left -= offset;
        right -= offset;
        if (left > right) {
            throw new IllegalArgumentException("Illegal Interval");
        } else if (right - left < 2) {
            return this;
        }
        if (isEmpty()) {
            appendPair(left, right);
            return this;
        }
        int lPos = findLastSmaller(left);
        int rPos = findFirstLarger(right);
        if (pairsLeft.size() == 1) {
            var left0 = pairsLeft.get(0);
            var right0 = pairsRight.get(0);
            if (right <= left0) {
                insertPair(0, left, right);
            } else if (left >= right0) {
                appendPair(left, right);
            } else {
                setPair(0, Math.min(left0, left), Math.max(right0, right));
            }
            return this;
        }

        int resetId;
        if (pairsLeft.size() > 1) {
            if (lPos + 1 > pairsLeft.size() - 1) {
                //没有东西给你修改
                resetId = -1;
            } else {
                if (lPos >= 0 && rPos >= 0 && rPos - lPos == 1) {
                    /*
                       p           [+++]
                       r    [++++]       [+++++]
                    */
                    insertPair(lPos + 1, left, right);
                    return this;
                }
                resetId = lPos < 0 ? 0 : lPos + 1;
            }
        } else {
            resetId = Math.max(lPos, 0);
        }
        int removeR = rPos < 0 ? pairsLeft.size() - 1 : rPos - 1;
        if (resetId == -1) {
            appendPair(left, right);
            resetId = lPos + 1;
        }
        int min = Math.min(pairsLeft.get(resetId), left);
        int max = Math.max(pairsRight.get(removeR), right);
        removeIntervals(resetId + 1, removeR);
        setPair(resetId ,min, max);
        return this;
    }

    /**
     * 获得一个整数和自己的并集
     * @param data 整数
     * @return 得到的并集
     */
    public OpenRange union(int data) {
        return union(data - 1, data + 1);
    }

    /**
     * 区间是否为空集
     * @return 空集返回true
     */
    public boolean isEmpty() {
        int size = pairsLeft.size();
        if (size > 1) {
            return false;
        }else if (size == 0) {
            pairsLeft.add(0);
            pairsRight.add(0);
            return true;
        } else {
            return pairsRight.get(0) - pairsLeft.get(0) < 2;
        }
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "()";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(getActualLeft(0))
                .append(',').append(getActualRight(0)).append(')');
        for (int i = 1; i < pairsLeft.size() ; i ++) {
            sb.append("U(")
                    .append(getActualLeft(i))
                    .append(',')
                    .append(getActualRight(i))
                    .append(')');
        }
        return sb.toString();
    }

    /**
     * 获得区间的复制
     * @return 复制得到的对象
     */
    public OpenRange copy() {
        var res = new OpenRange();
        res.pairsLeft = new ArrayList<>(this.pairsLeft);
        res.pairsRight = new ArrayList<>(this.pairsRight);
        res.offset = this.offset;
        return res;
    }

    /**
     * 成为输入区间的复制
     * @param source 输入区间
     */
    public void copy(OpenRange source){
        pairsRight = new ArrayList<>(source.pairsRight);
        pairsLeft = new ArrayList<>(source.pairsLeft);
        offset = source.offset;
    }

    /**
     * 将两个开区间取交集操作，返回结果
     * @param other 输入区间
     * @return 取交集后的集合
     */
    public OpenRange intersection(OpenRange other) {
        OpenRange result= new OpenRange();
        if (other.isEmpty() || this.isEmpty()) {
            return result;
        }
        int posA = 0;
        int posB = 0;
        int sizeA = other.pairsLeft.size();
        int sizeB = this.pairsLeft.size();
        while (posA < sizeA && posB < sizeB) {
            int leftA = other.getActualLeft(posA), rightA = other.getActualRight(posA);
            int leftB = this.getActualLeft(posB), rightB = this.getActualRight(posB);
            
            //无交集的情况
            if (rightB <= leftA) {
                //pairThis在左侧
                posB++;
                continue;
            }
            if (rightA <= leftB) {
                //pairThis在右侧
                posA++;
                continue;
            }
            //有交集
            int leftInters = Math.max(leftB, leftA);
            int rightInters = Math.min(rightB, rightA);
            if (leftInters != rightInters) {
                result.appendPair(leftInters, rightInters);
            }
            if (rightB <= rightA) {
                //pairThis偏左
                posB++;
            } else {
                //pairThis偏右
                posA++;
            }
        }
        return result;
    }

    /**
     * 在不改变输入区间的情况下，将两个开区间取并集操作，返回结果
     * @param other 输入区间
     * @return 取并集后的集合
     */
    public OpenRange union(OpenRange other) {
        if (other.isEmpty()) {
            return this.copy();
        }
        if (this.isEmpty()) {
            return other.copy();
        }
        OpenRange result = new OpenRange();
        int posA = 0;
        int posB = 0;
        int sizeA = other.pairsLeft.size();
        int sizeB = this.pairsLeft.size();
        boolean resetA = true, resetB = true;
        int leftA = 0,rightA = 0;
        int leftB = 0,rightB = 0;
        while (true) {
            if (resetA) {
                if (posA >= sizeA) {
                    result.appendPair(leftB, rightB);
                    break;
                }
                leftA = other.getActualLeft(posA);
                rightA = other.getActualRight(posA);
                resetA = false;
            }
            if (resetB) {
                if (posB >= sizeB) {
                    result.appendPair(leftA, rightA);
                    break;
                }
                leftB = getActualLeft(posB);
                rightB = getActualRight(posB);
                resetB = false;
            }
            //无交集的情况
            if (rightB <= leftA) {
                //pairB在左侧
                result.appendPair(leftB, rightB);
                resetB = true;
                posB++;
                continue;
            }
            if (rightA <= leftB) {
                //pairB在右侧
                result.appendPair(leftA, rightA);
                resetA = true;
                posA++;
                continue;
            }
            //有交集
            int leftUnion = Math.min(leftB, leftA);
            //int rightUnion = Math.max(rightB, rightA);
            if (rightA > rightB) {
                //pairA偏右
                leftA = leftUnion;
                //rightA = rightUnion;
                resetB = true;
                posB++;
            } else {
                leftB = leftUnion;
                //rightB = rightUnion;
                resetA = true;
                posA++;
            }
        }
        posA++;
        posB++;
        //把剩余部分直接加进去
        while (posA < sizeA) {
            result.pairsLeft.add(other.getActualLeft(posA));
            result.pairsRight.add(other.getActualRight(posA));
            posA++;
        }
        while (posB < sizeB) {
            result.pairsLeft.add(this.getActualLeft(posB));
            result.pairsRight.add(this.getActualRight(posB));
            posB++;
        }
        return result;
    }

    public boolean contains(OpenRange range) {
        if (range.isEmpty()) {
            return true;
        }
        int posThis = 0, posOther = 0;
        int sizeThis = pairsLeft.size(), sizeOther = range.pairsLeft.size();
        while (true) {
            if (posOther == sizeOther) {
                return true;
            }
            if (posThis == sizeThis) {
                return false;
            }
            int leftThis = getActualLeft(posThis), rightThis = getActualRight(posThis);
            int leftOther = range.getActualLeft(posOther), rightOther = range.getActualRight(posOther);
            //无交集
            if (leftOther >= rightThis) {
                posThis++;
                continue;
            }
            if (leftThis >= rightOther) {
                return false;
            }
            //有交集
            if (leftOther >= leftThis && rightOther <= rightThis) {
                //在区间内
                posOther++;
                posThis++;
                continue;
            }
            //不符合
            return false;
        }
    }

    public boolean contains(int left, int right) {
        left -= offset;
        right -= offset;
        if (isEmpty()) {
            return false;
        } else if (pairsLeft.size() == 1) {
            return containsRange(0, left, right);
        } else {
            int posR = findFirstLarger(right);
            if (posR == 0) {
                return containsRange(0, left, right);
            }
            return containsRange(posR < 0 ? pairsLeft.size() - 1 : posR - 1, left, right);
        }
    }

    public boolean contains(int value) {
        return contains(value - 1, value + 1);
    }

    public void forEach(BiConsumer<Integer, Integer> action) {
        if (isEmpty()) {
            return;
        }
        for (int i = 0 ; i < pairsLeft.size() ; i ++) {
            action.accept(pairsLeft.get(i), pairsRight.get(i));
        }
    }

    public int size() {
        if (isEmpty()) {
            return 0;
        }
        return pairsLeft.size();
    }

    public int lengthAt(int pos) {
        checkRange(pos);
        return pairsRight.get(pos) - pairsLeft.get(pos) - 1;
    }

    @Override
    public int hashCode() {
        return pairsLeft.hashCode() + pairsRight.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OpenRange range) {
            int size = this.size();
            if (range.size() != size) {
                return false;
            }
            for (int i = 0 ; i < size ; i ++) {
                if (!Objects.equals(pairsLeft.get(i), range.pairsLeft.get(i))) {
                    return false;
                }
                if (!Objects.equals(pairsRight.get(i), range.pairsRight.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void removeIntervals(int start, int end) {
        start = Math.max(start, 0);
        end = Math.min(end, pairsLeft.size() - 1);
        if (start < end) {
            pairsLeft.subList(start, end + 1).clear();
            pairsRight.subList(start, end + 1).clear();
        } else if (start == end) {
            pairsLeft.remove(start);
            pairsRight.remove(start);
        }
    }

    /**
     * 找到区间中第一个小于target的区间（右端点小于target左端点）
     *
     * @param targetL 目标单区间左端点
     * @return 找到的单区间索引，没找到返回-1
     */
    private int findLastSmaller(int targetL) {
        if (pairsLeft.size() == 1) {
            if (pairsRight.get(0) < targetL) {
                return 0;
            }
            return -1;
        }
        int left = 0, right = pairsLeft.size() - 2;
        //把intervals.get(middle)右侧的空当视为一个区间
        while (left <= right) {
            int middle = (left + right) / 2;
            if (pairsRight.get(middle) > targetL){
                right = middle - 1; //target在左侧
            } else if (pairsRight.get(middle + 1) <= targetL) {
                left = middle + 1;//target在右侧
            } else {
                return middle;
            }
        }
        if (left >= pairsLeft.size() - 1) {
            return pairsLeft.size() - 1;
        }
        return -1;
    }


    /**
     * 找到区间中第一个大于target的单区间（左端点大于target右端点）
     *
     * @param targetR 目标单区间右端点
     * @return 找到的单区间索引，没找到返回-1
     */
    private int findFirstLarger(int targetR) {
        if (pairsLeft.size() == 1) {
            if (pairsLeft.get(0) > targetR) {
                return 0;
            }
            return -1;
        }
        int left = 1, right = pairsLeft.size() - 1;
        //把intervals.get(middle)左侧的空当视为一个区间
        while (left <= right) {
            int middle = (left + right) / 2;
            if (pairsLeft.get(middle - 1) >= targetR) {
                right = middle - 1; //target在左侧
            } else if (pairsLeft.get(middle) < targetR) {
                left = middle + 1; //target在右侧
            } else {
                return middle;
            }
        }
        if (right <= 0) {
            return 0;
        }
        return -1;
    }

    private boolean containsRange(int pos, int left, int right) {
        return left >= pairsLeft.get(pos) && right <= pairsRight.get(pos);
    }
    
    private void appendPair(int left, int right) {
        if (isEmpty()) {
            pairsLeft.set(0, left);
            pairsRight.set(0, right);
        } else {
            pairsLeft.add(left);
            pairsRight.add(right);
        }
    }
    
    private void insertPair(int pos, int left, int right) {
        pairsLeft.add(pos, left);
        pairsRight.add(pos, right);
    }
    
    private void setPair(int pos, int left, int right) {
        pairsLeft.set(pos, left);
        pairsRight.set(pos, right);
    }

    private int getActualLeft(int pos) {
        return pairsLeft.get(pos) + offset;
    }

    private int getActualRight(int pos) {
        return pairsRight.get(pos) + offset;
    }

    private void checkRange(int pos) {
        if (isEmpty()) {
            throw new IllegalArgumentException("empty range");
        }
        if (pos < 0 || pos >= pairsLeft.size()) {
            throw new IllegalArgumentException("acquired:[0," + pairsLeft.size() + ")  actual:" + pos);
        }
    }
}
