package stupidcoder.compile.lex;

@FunctionalInterface
public interface ICharPredicate {
    boolean accept(int input);

    static ICharPredicate single(int ch) {
        return c -> c == ch;
    }

    static ICharPredicate and(ICharPredicate c1, ICharPredicate c2) {
        return c -> c1.accept(c) && c2.accept(c);
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

    static ICharPredicate ranged(int start, int end) {
        return c -> c >= start && c <= end;
    }
}
