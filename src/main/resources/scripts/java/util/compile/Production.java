package stupidcoder.util.compile;

import stupidcoder.util.compile.symbol.Symbol;

import java.util.List;

public record Production(int id, Symbol head, List<Symbol> body) {

    public Symbol symbolAt(int i) {
        if (i < 0 || i >= body.size()) {
            return null;
        }
        return body.get(i);
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
