package stupidcoder.grammar.internal;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;
import stupidcoder.grammar.IPriorityRegistry;

import java.util.List;

public class PriorityManager implements IPriorityAccess, IPriorityRegistry {
    private int[] symbolPriorities;
    private int[] prodPriorities;
    private final IGrammarAccess access;

    public PriorityManager(IGrammarAccess access) {
        this.access = access;
    }

    public void init() {
        symbolPriorities = new int[access.symbolsCount()];
        prodPriorities = new int[access.grammar().size()];
        for (Production production : access.grammar()) {
            registerGrammar(production);
        }
    }

    private void registerGrammar(Production production) {
        List<Symbol> symbols = production.production();
        for (int i = symbols.size() - 1 ; i >= 0 ; i--) {
            Symbol s = symbols.get(i);
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
    public int compare(Symbol s, Production g) {
        return symbolPriorities[s.id] - prodPriorities[g.id()];
    }
}
