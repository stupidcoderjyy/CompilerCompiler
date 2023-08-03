package com.stupidcoder.cc;

import com.stupidcoder.cc.lex.NFARegexParser;
import com.stupidcoder.cc.util.input.StringInput;

public class Main {
    public static void main(String[] args) {
        NFARegexParser.parse(new StringInput("([a-z]ab)*|[a-z]+"));
    }
}
