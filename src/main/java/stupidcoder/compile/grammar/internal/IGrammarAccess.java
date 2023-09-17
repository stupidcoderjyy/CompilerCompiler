package stupidcoder.compile.grammar.internal;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IGrammarAccess {
    List<Production> productionsWithHead(Symbol head);
    Production root();
    Symbol symbolOf(String lexeme);
    List<Production> grammar();
    int symbolsCount();
    int nonTerminalCount();
    int terminalCount();
    boolean calcForward(Set<Symbol> result, Production g, int startPos);
    Map<Integer, Integer> getTerminalIdRemap();
}
