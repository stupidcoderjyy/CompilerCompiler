package com.stupidcoder.cc.template.tokens;

import com.stupidcoder.cc.template.IToken;
import com.stupidcoder.cc.template.TokenTypes;

public class TokenInteger implements IToken {
    private long value;
    private boolean isLong;

    @Override
    public int type() {
        return TokenTypes.INTEGER;
    }

    @Override
    public IToken fromLexeme(String lexeme) {
        char suffix = lexeme.charAt(lexeme.length() - 1);
        isLong = suffix == 'L' || suffix == 'l';
        if (isLong) {
            lexeme = lexeme.substring(0, lexeme.length() - 1);
        }
        value = lexeme.startsWith("0x") ?
                Long.parseLong(lexeme.substring(2), 16) :
                Long.parseLong(lexeme);
        if (!isLong && value > Integer.MAX_VALUE) {
            isLong = true;
        }
        return this;
    }

    public long getValue() {
        return value;
    }

    public boolean isLong() {
        return isLong;
    }
}
