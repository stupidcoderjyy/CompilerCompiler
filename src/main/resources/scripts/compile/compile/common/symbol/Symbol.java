package stupidcoder.common.symbol;

public class Symbol  {
    public final boolean isTerminal;
    public int id;
    private final String debugLexeme;

    public Symbol(String debugLexeme, boolean isTerminal, int id) {
        this.debugLexeme = debugLexeme;
        this.isTerminal = isTerminal;
        this.id = id;
    }

    @Override
    public String toString() {
        return debugLexeme;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof stupidcoder.util.common.symbol.Symbol s) {
            return s.isTerminal == isTerminal && s.id == id;
        }
        return false;
    }
}
