package com.stupidcoder.cc.lex.core;

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
        if (c1 == null) {
            return c2;
        }
        if (c2 == null) {
            return c1;
        }
        return c -> c1.accept(c) || c2.accept(c);
    }

    static ICharPredicate or(byte b1, byte b2) {
        return c -> c == b1 || c == b2;
    }

    static ICharPredicate and(ICharPredicate p1, ICharPredicate p2) {
        if (p1 == null) {
            return p2;
        }
        if (p2 == null) {
            return p1;
        }
        return c -> p1.accept(c) && p2.accept(c);
    }

    static ICharPredicate ranged(byte start, byte end) {
        return c -> c >= start && c <= end;
    }
}
