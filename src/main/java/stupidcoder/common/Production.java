package stupidcoder.common;

import stupidcoder.common.symbol.Symbol;

import java.util.List;

public class Production {
    private final int id;
    private final Symbol head;
    private final List<Symbol> production;

    public Production(int id, Symbol head, List<Symbol> production) {
        this.id = id;
        this.head = head;
        this.production = production;
    }

    public int id() {
        return id;
    }

    public Symbol head() {
        return head;
    }

    public Symbol symbolAt(int i) {
        if (i < 0 || i >= production.size()) {
            return null;
        }
        return production.get(i);
    }

    public List<Symbol> production() {
        return production;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(head).append(" →");
        for (Symbol symbol : production) {
            sb.append(' ').append(symbol);
        }
        return sb.toString();
    }
}
