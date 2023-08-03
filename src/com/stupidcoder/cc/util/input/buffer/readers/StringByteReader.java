package com.stupidcoder.cc.util.input.buffer.readers;

import com.stupidcoder.cc.util.input.buffer.IByteReader;

import java.nio.charset.StandardCharsets;

/**
 * @author stupid_coder_jyy
 */
public class StringByteReader implements IByteReader {
    private String str;

    private byte[] bytes;

    private int next;

    public StringByteReader(String str) {
        this.str = str;
    }

    public void setString(String str) {
        this.str = str;
        open();
    }

    @Override
    public boolean open() {
        if (str == null) {
            str = "";
        }
        bytes = str.getBytes(StandardCharsets.UTF_8);
        next = 0;
        return true;
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
