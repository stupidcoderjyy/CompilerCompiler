package com.stupidcoder.cc;

public class Main {
    public static void main(String[] args) {
        long l1 = System.currentTimeMillis();
        new CompilerInstaller()
                .registerToken("@d+|0[Xx]@h+", "integer")
                .registerToken("@d+(.@d+|E[+-]?@d+)F?", "double")
                .registerToken("<<", "left_shift")
                .registerSingleToken("<")
                .registerSingleToken(">")
                .registerKeyWord("for")
                .registerKeyWord("while")
                .build();
        System.out.println(System.currentTimeMillis() - l1);
    }
}
