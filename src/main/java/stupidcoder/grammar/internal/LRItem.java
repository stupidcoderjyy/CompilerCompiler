package stupidcoder.grammar.internal;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;

import java.util.HashSet;
import java.util.Set;

public class LRItem {
    public final Production production;
    public final int point;
    public final Set<Symbol> forwardSymbols = new HashSet<>();
    protected final int hash;
    public int id;

    public LRItem(Production production, int point, int id) {
        this.production = production;
        this.point = point;
        this.hash = calcHash(production, point);
        this.id = id;
    }

    public LRItem(Production production, int point) {
        this(production, point, -1);
    }

    public LRItem(LRItem other, int id) {
        this(other.production, other.point, id);
        forwardSymbols.addAll(other.forwardSymbols);
    }

    public LRItem(LRItem other) {
        this(other, -1);
    }

    public final Symbol nextSymbol() {
        return production.symbolAt(point);
    }

    public final boolean reachEnd() {
        return point == production.production().size();
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LRItem item) {
            if (item.id < 0 || id < 0) {
                return item.hash == hash;
            } else {
                return item.hash == hash && item.id == id;
            }
        }
        return false;
    }


    public static int calcHash(Production g, int point) {
        return (point << 16) | g.id();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(production.head()).append(" →");
        int curPos = 0;
        while (curPos < production.production().size()) {
            if (curPos == point) {
                sb.append(" ·");
            }
            sb.append(' ').append(production.production().get(curPos));
            curPos++;
        }
        if (point == curPos) {
            sb.append(" ·");
        }
        return '<' + sb.toString() + ", " + forwardSymbols + '>';
    }
}
