package stupidcoder.util.input.buffer.readers;

import stupidcoder.util.input.buffer.IByteReader;

import java.nio.charset.StandardCharsets;

/**
 * @author stupid_coder_jyy
 */
public class StringByteReader implements IByteReader {
    private String str;

    private byte[] bytes;

    private int next;

    public StringByteReader(String str) {
        if (str == null) {
            throw new NullPointerException("null string");
        }
        this.str = str;
        bytes = str.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        bytes = null;
        str = null;
    }

    @Override
    public int read(byte[] arr, int offset, int len) {
        int actualLen = Math.min(len, bytes.length - next);
        System.arraycopy(bytes, next, arr, offset, actualLen);
        next += actualLen;
        return actualLen;
    }
}
