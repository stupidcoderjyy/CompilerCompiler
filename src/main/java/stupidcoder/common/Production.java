package stupidcoder.common;

import stupidcoder.common.symbol.Symbol;

import java.util.List;

public class Production {
    private final int id;
    private final Symbol head;
    private final List<Symbol> body;

    public Production(int id, Symbol head, List<Symbol> body) {
        this.id = id;
        this.head = head;
        this.body = body;
    }

    public int id() {
        return id;
    }

    public Symbol head() {
        return head;
    }

    public Symbol symbolAt(int i) {
        if (i < 0 || i >= body.size()) {
            return null;
        }
        return body.get(i);
    }

    public List<Symbol> body() {
        return body;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(head).append(" →");
        for (Symbol symbol : body) {
            sb.append(' ').append(symbol);
        }
        return sb.toString();
    }
}
