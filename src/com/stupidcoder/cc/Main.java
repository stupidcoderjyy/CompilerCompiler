package com.stupidcoder.cc;

import com.stupidcoder.cc.lex.core.DFABuilder;
import com.stupidcoder.cc.lex.core.IDfaSetter;
import com.stupidcoder.cc.lex.core.NFARegexParser;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        long l1 = System.currentTimeMillis();
        new CompilerInstaller()
                .registerToken("@d+|0[Xx]@h+", "integer")
                .registerToken("@a@w*", "word")
                .registerToken("@d+(.@d+|E[+-]?@d+)F?", "double")
                .registerKeyWord("for")
                .registerKeyWord("while")
                .build();
        System.out.println(System.currentTimeMillis() - l1);
    }
}
