package stupidcoder.util.input;

import java.nio.charset.StandardCharsets;
import java.util.Stack;

public class StringInput implements IInput {
    private final byte[] data;
    private int forward;
    private final Stack<Integer> marks = new Stack<>();

    public StringInput(String str) {
        data = str.getBytes(StandardCharsets.UTF_8);
        forward = 0;
        mark();
    }

    @Override
    public boolean isOpen() {
        return data != null;
    }

    @Override
    public boolean available() {
        checkOpen();
        return forward < data.length;
    }

    @Override
    public int read() {
        checkAvailable();
        return data[forward++];
    }

    @Override
    public int read(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("read count should be positive:" + count);
        }
        forward += count - 1;
        checkAvailable();
        return data[forward++];
    }

    @Override
    public void mark() {
        marks.push(forward);
    }

    @Override
    public void removeMark() {
        if (!marks.empty()) {
            marks.pop();
        }
    }

    @Override
    public void recover(boolean consume) {
        if (!marks.empty()) {
            forward = consume ? marks.pop() : marks.peek();
        }
    }

    @Override
    public String capture() {
        return switch (marks.size()) {
            case 1 -> capture(forward, marks.pop());
            case 0 -> capture(0, marks.pop());
            default -> capture(marks.pop(), marks.pop());
        };
    }

    private String capture(int end, int start) {
        return new String(data, start, end - start, StandardCharsets.UTF_8);
    }

    @Override
    public int retract() {
        forward = Math.max(0, forward - 1);
        return data[forward];
    }
}
