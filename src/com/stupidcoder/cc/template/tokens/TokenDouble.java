package com.stupidcoder.cc.template.tokens;

import com.stupidcoder.cc.template.IToken;
import com.stupidcoder.cc.template.TokenTypes;

public class TokenDouble implements IToken {
    private double value;

    @Override
    public int type() {
        return TokenTypes.DOUBLE;
    }

    @Override
    public IToken fromLexeme(String lexeme) {
        value = Double.parseDouble(lexeme);
        return this;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "double:" + value;
    }
}
