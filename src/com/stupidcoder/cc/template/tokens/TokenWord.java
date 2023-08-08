package com.stupidcoder.cc.template.tokens;

import com.stupidcoder.cc.template.IToken;
import com.stupidcoder.cc.template.TokenTypes;

import java.util.HashMap;
import java.util.Map;

public class TokenWord implements IToken {
    private static final Map<String, Integer> keyWords;

    static {
        keyWords = new HashMap<>();
        init();
    }

    private int type;

    @Override
    public int type() {
        return type;
    }

    @Override
    public IToken fromLexeme(String lexeme) {
        type = keyWords.getOrDefault(lexeme, TokenTypes.ID);
        return this;
    }

    private static void init() {
        keyWords.put("for", TokenTypes.KEY_WORD_FOR);
        //...
    }
}
