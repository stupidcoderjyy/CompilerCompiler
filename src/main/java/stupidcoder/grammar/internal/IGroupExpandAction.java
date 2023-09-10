package stupidcoder.grammar.internal;

import stupidcoder.common.symbol.Symbol;

import java.util.List;
import java.util.Map;

public interface IGroupExpandAction {
    void onGroupExpandFinished(GroupTemp baseTemp, CoreGroup baseSrc, Map<Symbol, List<LRItem>> nextSymbolToItems);
}
