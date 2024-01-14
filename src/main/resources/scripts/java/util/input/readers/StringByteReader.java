package stupidcoder.util.input.readers;

import stupidcoder.util.input.IByteReader;

import java.nio.charset.StandardCharsets;

public class StringByteReader implements IByteReader {
    private byte[] bytes;

    private int next;

    public StringByteReader(String str) {
        if (str == null) {
            throw new NullPointerException("null string");
        }
        bytes = str.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        bytes = null;
    }

    @Override
    public int read(byte[] arr, int offset, int len) {
        int actualLen = Math.min(len, bytes.length - next);
        System.arraycopy(bytes, next, arr, offset, actualLen);
        next += actualLen;
        return actualLen;
    }
}
