package stupidcoder.compile.grammar;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.common.symbol.Symbol;

import java.util.List;

class PriorityManager implements IPriorityAccess, IPriorityRegistry {
    private int[] symbolPriorities;
    private int[] prodPriorities;
    private final IGrammarAccess access;

    PriorityManager(IGrammarAccess access) {
        this.access = access;
    }

    void init() {
        symbolPriorities = new int[access.symbolsCount()];
        prodPriorities = new int[access.grammar().size()];
        for (Production production : access.grammar()) {
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

    @Override
    public void registerPriority(String larger, String smaller, int difference) {
        int pSmaller = symbolPriorities[access.symbolOf(smaller).id];
        symbolPriorities[access.symbolOf(larger).id] = pSmaller + difference;
    }

    @Override
    public int compare(Symbol s, Production p) {
        return symbolPriorities[s.id] - prodPriorities[p.id()];
    }
}
