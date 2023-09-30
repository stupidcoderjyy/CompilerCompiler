package stupidcoder.compile.syntax;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;

import java.util.HashSet;
import java.util.Set;

class LRItem {
    final Production production;
    final int point;
    final Set<Symbol> forwardSymbols = new HashSet<>();
    final int hash;
    int id;

    LRItem(Production production, int point, int id) {
        this.production = production;
        this.point = point;
        this.hash = calcHash(production, point);
        this.id = id;
    }

    LRItem(Production production, int point) {
        this(production, point, -1);
    }

    final Symbol nextSymbol() {
        return production.symbolAt(point);
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

    static int calcHash(Production g, int point) {
        return (point << 16) | g.id();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(production.head()).append(" →");
        int curPos = 0;
        while (curPos < production.body().size()) {
            if (curPos == point) {
                sb.append(" ·");
            }
            sb.append(' ').append(production.body().get(curPos));
            curPos++;
        }
        if (point == curPos) {
            sb.append(" ·");
        }
        return '<' + sb.toString() + ", " + forwardSymbols + '>';
    }
}
