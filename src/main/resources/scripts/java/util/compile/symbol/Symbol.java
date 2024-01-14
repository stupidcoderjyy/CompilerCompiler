package stupidcoder.util.compile.symbol;

public class Symbol  {
    public final boolean isTerminal;
    public int id;
    private final String name;

    public Symbol(String name, boolean isTerminal, int id) {
        this.name = name;
        this.isTerminal = isTerminal;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol s) {
            return s.isTerminal == isTerminal && s.id == id;
        }
        return false;
    }
}
