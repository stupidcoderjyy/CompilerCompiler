package stupidcoder.util.input;

import java.nio.charset.StandardCharsets;

public interface IInput {

    /**
     * 输入系统是否启动
     * @return 启动返回true
     */
    boolean isOpen();

    /**
     * 是否可以继续读取
     * @return 可以读取返回true
     */
    boolean available();

    /**
     * 读取一个字节
     * @return 下一个字节
     * @throws InputException 在无法读取或者词素长度超过限制时抛出
     */
    int read();

    /**
     * 连续读取若干字节
     * @param count 跳过的字节数
     * @return 最后读取的字节
     * @throws InputException 在无法读取或者词素长度超过限制时抛出
     * @throws IllegalArgumentException count不为正时抛出
     */
    default int read(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count should be positive:" + count);
        }
        for (int i = 0 ; i < count - 1 ; i++) {
            read();
        }
        return read();
    }

    /**
     * 读取一个无符号字节
     * @return 无符号字节
     */
    default int readUnsigned() {
        return read() & 0xFF;
    }

    /**
     * 为输入系统添加一个位置标记。无效的标记会被自动清楚
     */
    void mark();

    /**
     * 移除最近添加的位置标记
     */
    void removeMark();

    /**
     * 将输入系统的位置恢复到最近添加的位置标记处，并移除标记
     */
    default void recover() {
        recover(false);
    }

    /**
     * 将输入系统的位置恢复到最近添加的位置标记处
     * @param consume 是否移除标记，移除传入true
     */
    void recover(boolean consume);

    /**
     * 截取一段字节。若存在两个有效的标记，则截取这两个标记之间的内容；若存在一个有效的标记，则截取从标记到当前位置的内容。
     * 如果不存在有效的标记，则返回一个非空的值
     * @return 字节组成的UTF-8字符串
     */
    String capture();

    /**
     * 回退一个字节
     * @return 回退过程中经过的最后一个字节（调用{@link IInput#read()}后得到的字节）
     * @throws InputException 回退失败
     */
    int retract();

    /**
     * 回退若干字节
     * @return 回退过程中经过的最后一个字节（调用{@link IInput#read()}后得到的字节）
     * @throws InputException 回退失败
     */
    default int retract(int count) {
        if (count <= 0) {
            return -1;
        }
        count--;
        for (int i = 0 ; i < count ; i ++) {
            retract();
        }
        return retract();
    }

    /**
     * 读取一个UTF字符
     * @return UTF字符
     */
    default String readUtfChar() {
        byte[] data;
        int b1 = readUnsigned();
        switch (b1 >> 4) {
            case 0, 1, 2, 3, 4, 5, 6, 7 -> data = new byte[]{(byte) b1};
            case 12, 13 -> {
                //110x xxxx 10xx xxxx
                int b2 = readUnsigned();
                if ((b2 & 0xC0) != 0x80) {
                    throw new InputException("malformed input:" + Integer.toBinaryString((b1 << 8) | b2));
                }
                data = new byte[] {(byte) b1, (byte) b2};
            }
            case 14 -> {
                //1110 xxxx 10xx xxxx 10xx xxxx
                int b2 = readUnsigned();
                int b3 = readUnsigned();
                if ((b2 & 0xC0) != 0x80 || (b3 & 0xC0) != 0x80) {
                    throw new InputException("malformed input:" +
                            Integer.toBinaryString((b1 << 16) | (b2 << 8) | b3));
                }
                data = new byte[]{(byte) b1, (byte) b2, (byte) b3};
            }
            default -> throw new InputException("malformed input:" + Integer.toBinaryString(b1));
        }
        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * 不断读取字符，直到遇见目标字符或无法继续读取
     * @param ch 目标字符
     */
    default void find(int ch) {
        while (available()) {
            if (read() == ch) {
                break;
            }
        }
    }

    /**
     * 不断读取字符，直到遇见目标字符或无法继续读取
     * @param chs 目标字符，位于[0, 128)外的会被忽略
     * @return 找到的那个字符，没找到返回-1
     */
    default int find(int ... chs) {
        return find(BitClass.of(chs));
    }

    /**
     * 不断读取字符，直到遇见目标字符或无法继续读取
     * @param clazz 字符集合
     * @return 找到的那个字符，没找到返回-1
     */
    default int find(BitClass clazz) {
        while (available()) {
            int b = read();
            if (clazz.accept(b)) {
                return b;
            }
        }
        return -1;
    }

    /**
     * 不断读取字符，直到遇见非目标字符或无法继续读取
     * @param ch – 目标字符
     * @return 最后跳过的字符，没找到返回-1
     */
    default int skip(int ch) {
        int pre = -1;
        while (available()) {
            int b = read();
            if (b == ch) {
                pre = b;
                continue;
            }
            retract();
            break;
        }
        return pre;
    }

    /**
     * 不断读取字符，直到遇见非目标字符或无法继续读取
     * @param chs 目标字符，位于[0, 128)外的会被忽略
     * @return 最后跳过的字符，没找到返回-1
     */
    default int skip(int ... chs) {
        return skip(BitClass.of(chs));
    }

    /**
     * 不断读取字符，直到遇见非目标字符或无法继续读取
     * @param clazz 字符集合
     * @return 最后跳过的字符，没找到返回-1
     */
    default int skip(BitClass clazz) {
        int pre = -1;
        while (available()) {
            int b = read();
            if (clazz.accept(b)) {
                pre = b;
                continue;
            }
            retract();
            break;
        }
        return pre;
    }

    default void checkOpen() {
        if (!isOpen()) {
            throw new InputException("closed");
        }
    }

    default void checkAvailable() {
        if (!available()) {
            throw new InputException("not available");
        }
    }
}
