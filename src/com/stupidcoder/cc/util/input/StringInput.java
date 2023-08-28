package com.stupidcoder.cc.util.input;

import java.nio.charset.StandardCharsets;

public class StringInput implements ILexerInput{
    private final String str;
    private byte[] data;
    private int forward;
    private int lexemeStart;

    public StringInput(String str) {
        this.str = str;data = str.getBytes(StandardCharsets.UTF_8);
        forward = 0;
        lexemeStart = 0;
    }

    @Override
    public boolean isOpen() {
        return data != null;
    }

    @Override
    public void close() {
        data = null;
    }

    @Override
    public void markLexemeStart() {
        lexemeStart = forward;
    }

    @Override
    public boolean available() {
        checkOpen();
        return hasNext();
    }

    @Override
    public int read() {
        checkAvailable();
        return data[forward++];
    }

    @Override
    public int readUnsigned() {
        return read() & 0xFF;
    }

    @Override
    public boolean hasNext() {
        return forward < str.length();
    }

    @Override
    public String lexeme() {
        String str = this.str.substring(lexemeStart, forward);
        lexemeStart = forward;
        return str;
    }

    @Override
    public void retract(int count) {
        forward = Math.max(0, forward - count);
        lexemeStart = Math.min(forward, lexemeStart);
    }
}
