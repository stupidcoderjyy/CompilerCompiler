package com.stupidcoder.cc.lex;

@FunctionalInterface
public interface ICharPredicate {
    boolean accept(byte input);

    static ICharPredicate single(byte ch) {
        return c -> c == ch;
    }

    static ICharPredicate single(char ch) {
        return c -> c == ch;
    }

    static ICharPredicate or(ICharPredicate c1, ICharPredicate c2) {
        return c -> c1.accept(c) || c2.accept(c);
    }

    static ICharPredicate ranged(byte start, byte end) {
        return c -> c >= start && c <= end;
    }
}
