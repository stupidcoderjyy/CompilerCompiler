package stupidcoder.util.input;

import stupidcoder.util.input.readers.ConsoleByteReader;
import stupidcoder.util.input.readers.FileByteReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

public class BufferedInput implements IInput, AutoCloseable {
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final int MAX_BUFFER_SIZE = 4096;
    private final IByteReader reader;
    protected final int bufEndA, bufEndB;
    protected Deque<Integer> marks;
    protected int inputEnd = -1;
    protected byte[] buffer;
    protected int forward;
    protected int fillCount;

    public BufferedInput(IByteReader reader, int bufSize) {
        if (bufSize <= 0 || bufSize > MAX_BUFFER_SIZE) {
            throw new IllegalArgumentException("invalid size:" + bufSize + ", required:(0, 4096]");
        }
        if (reader == null) {
            throw new NullPointerException("null reader");
        }
        this.buffer = new byte[bufSize * 2];
        this.forward = 0;
        this.reader = reader;
        this.bufEndA = bufSize;
        this.bufEndB = bufSize * 2;
        init();
    }

    protected void init() {
        marks = new ArrayDeque<>();
        fillA();
        mark();
    }

    public BufferedInput(IByteReader reader) {
        this(reader, DEFAULT_BUFFER_SIZE);
    }

    public static BufferedInput fromFile(String filePath, int bufSize) {
        try {
            return new BufferedInput(new FileByteReader(filePath), bufSize);
        } catch (FileNotFoundException e) {
            throw new InputException(null, e);
        }
    }

    public static BufferedInput fromFile(String filePath) {
        return fromFile(filePath, DEFAULT_BUFFER_SIZE);
    }

    public static BufferedInput fromResource(String srcPath, int bufSize) {
        try {
            InputStream stream = BufferedInput.class.getResourceAsStream(srcPath);
            if (stream == null) {
                throw new FileNotFoundException(srcPath);
            }
            return new BufferedInput(new FileByteReader(stream), bufSize);
        } catch (FileNotFoundException e) {
            throw new InputException(null, e);
        }
    }

    public static BufferedInput fromResource(String srcPath) {
        return fromResource(srcPath, DEFAULT_BUFFER_SIZE);
    }

    public static BufferedInput fromConsole(int bufSize) {
        return new BufferedInput(new ConsoleByteReader(), bufSize);
    }

    public static BufferedInput fromConsole() {
        return new BufferedInput(new ConsoleByteReader());
    }

    @Override
    public boolean isOpen() {
        return buffer != null;
    }

    @Override
    public boolean available() {
        checkOpen();
        if (inputEnd < 0) {
            return true;
        }
        return forward != inputEnd;
    }

    @Override
    public int read() {
        checkAvailable();
        byte result = buffer[forward];
        forward++;
        if (forward == bufEndB) {
            forward = 0;
            if ((fillCount & 1) == 0) {
                fillA();
            }
        } else if (forward == bufEndA) {
            if ((fillCount & 1) == 1) {
                fillB();
            }
        }
        return result;
    }

    @Override
    public void mark() {
        marks.addFirst(forward);
    }

    @Override
    public void removeMark() {
        marks.pollFirst();
    }

    @Override
    public void recover(boolean consume) {
        if (!marks.isEmpty()) {
            forward = consume ? marks.pollFirst() : marks.getFirst();
        }
    }

    protected void fillA() {
        fillCount++;
        int size = reader.read(buffer, 0, bufEndA);
        if (size < bufEndA) {
            inputEnd = size;
        }
        while (!marks.isEmpty() && marks.getLast() < bufEndA) {
            marks.removeLast();
        }
    }

    protected void fillB() {
        fillCount++;
        int size = reader.read(buffer, bufEndA, bufEndA);
        if (size < bufEndA) {
            inputEnd = bufEndA + size;
        }
        while (!marks.isEmpty() && marks.getLast() >= bufEndA) {
            marks.removeLast();
        }
    }

    @Override
    public String capture() {
        checkOpen();
        return switch (marks.size()) {
            case 1 -> {
                int start = marks.getFirst();
                removeMark();
                yield capture(forward, start);
            }
            case 0 -> "";
            default -> {
                int end = marks.getFirst();
                removeMark();
                int start = marks.getFirst();
                removeMark();
                yield capture(end, start);
            }
        };
    }

    @Override
    public int retract() {
        checkOpen();
        if (forward == 0) {
            if (fillCount == 1 || (fillCount & 1) == 0) {
                throw new InputException("exceed retract limit");
            }
            forward = bufEndB - 1;
        } else if (forward == bufEndA) {
            if ((fillCount & 1) == 1) {
                throw new InputException("exceed retract limit");
            }
            forward--;
        } else {
            forward--;
        }
        return buffer[forward];
    }

    @Override
    public void close() {
        buffer = null;
        reader.close();
    }

    String capture(int end, int start) {
        if (start < end) {
            return new String(buffer, start, end - start, StandardCharsets.UTF_8);
        } else if (start > end){
            int lenB = bufEndB - start;
            byte[] temp = new byte[lenB + end];
            System.arraycopy(buffer, start, temp, 0, lenB);
            System.arraycopy(buffer, 0, temp, lenB, end);
            return new String(temp, StandardCharsets.UTF_8);
        }
        return "";
    }

    /**
     * 不断读取字符，直到下一个字符为目标字符
     * @param ch 目标字符
     * @return 即将遇到的目标字符
     */
    public int approach(int ch) {
        while (available()) {
            if (buffer[forward] == ch) {
                return ch;
            }
            read();
        }
        return -1;
    }

    /**
     * 不断读取字符，直到下一个字符为目标字符之一
     * @param clazz 目标字符集合
     * @return 即将遇到的目标字符
     */
    public int approach(BitClass clazz) {
        while (available()) {
            if (clazz.accept(buffer[forward])) {
                return buffer[forward];
            }
            read();
        }
        return -1;
    }

    /**
     * 不断读取字符，直到下一个字符为目标字符之一
     * @param chs 目标字符
     * @return 即将遇到的目标字符
     */
    public int approach(int ... chs) {
        return approach(BitClass.of(chs));
    }
}
