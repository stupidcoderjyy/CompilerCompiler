package com.stupidcoder.cc.util.input;

import java.nio.charset.StandardCharsets;

public class StringInput implements ILexerInput{
    private final String str;
    private byte[] data;
    private int forward;
    private int lexemeStart;
    private boolean isOpen;

    public StringInput(String str) {
        this.str = str;
        open();
    }

    @Override
    public void open() {
        data = str.getBytes(StandardCharsets.UTF_8);
        forward = 0;
        lexemeStart = 0;
        isOpen = true;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void close() {
        isOpen = false;
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
    public byte next() {
        checkAvailable();
        return data[forward++];
    }

    @Override
    public boolean hasNext() {
        return forward < str.length();
    }

    @Override
    public String lexeme() {
        lexemeStart = forward;
        return str.substring(lexemeStart, forward);
    }

    @Override
    public void retract(int count) {
        forward = Math.max(0, forward - count);
        lexemeStart = Math.min(forward, lexemeStart);
    }
}
