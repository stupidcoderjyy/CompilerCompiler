package stupidcoder.compile.syntax;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;

import java.util.List;
import java.util.Map;

public interface ISyntaxAccess {
    List<Production> productionsWithHead(Symbol head);
    Production root();
    List<Production> syntax();
    Map<String, Symbol> lexemeToSymbol();
    Map<Integer, Integer> terminalIdRemap();
    int terminalSymbolsCount();
    int nonTerminalSymbolsCount();
    boolean shouldReduce(Production target, Symbol forward);
}
