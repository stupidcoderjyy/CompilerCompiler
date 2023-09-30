package stupidcoder.compile.syntax;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.common.symbol.Symbol;

import java.util.List;

class PriorityManager implements IPriorityAccess {
    private int[] symbolPriorities;
    private int[] prodPriorities;
    private final SyntaxLoader loader;

    PriorityManager(SyntaxLoader loader) {
        this.loader = loader;
    }

    void init() {
        symbolPriorities = new int[loader.lexemeToSymbol.size()];
        prodPriorities = new int[loader.syntax().size()];
        for (Production production : loader.syntax()) {
            registerGrammar(production);
        }
    }

    private void registerGrammar(Production production) {
        List<Symbol> symbols = production.body();
        for (int i = symbols.size() - 1 ; i >= 0 ; i--) {
            Symbol s = symbols.get(i);
            if (s == DefaultSymbols.EPSILON) {
                continue;
            }
            if (s.isTerminal) {
                prodPriorities[production.id()] = symbolPriorities[s.id];
                return;
            }
        }
    }

   void registerPriority(String larger, String smaller, int difference) {
        int pSmaller = symbolPriorities[loader.lexemeToSymbol.get(smaller).id];
        symbolPriorities[loader.lexemeToSymbol.get(larger).id] = pSmaller + difference;
    }

    @Override
    public int compare(Symbol s, Production p) {
        return symbolPriorities[s.id] - prodPriorities[p.id()];
    }
}
