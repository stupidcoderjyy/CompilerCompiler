package com.stupidcoder.cc.template;

public interface IToken {
    int type();
    IToken fromLexeme(String lexeme);
}
