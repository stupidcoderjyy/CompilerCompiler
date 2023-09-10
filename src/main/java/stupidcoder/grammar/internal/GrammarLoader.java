package stupidcoder.grammar.internal;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.common.symbol.Symbol;
import stupidcoder.grammar.IProductionRegistry;
import stupidcoder.util.ArrayUtil;

import java.util.*;

public class GrammarLoader implements IProductionRegistry, IGrammarAccess {
    private final List<List<Production>> symbolToProductions = new ArrayList<>();
    protected final List<Production> productions = new ArrayList<>();
    private final Map<String, Symbol> lexemeToSymbol = new HashMap<>();
    private final Map<Integer, Integer> terminalIdRemap = new HashMap<>();
    private Production extendedRoot;
    private int productionCount = 0;
    private int terminalCount = 1;
    private int nonTerminalCount = 1;
    private Symbol tempStart;
    private List<Symbol> tempProduction = new ArrayList<>();

    public GrammarLoader() {
        symbolToProductions.add(new ArrayList<>());
    }

    @Override
    public IProductionRegistry begin(String lexeme) {
        tempStart = registerNonTerminal(lexeme);
        if (extendedRoot == null) {
            addProduction(DefaultSymbols.ROOT, List.of(tempStart));
            extendedRoot = productions.get(0);
        }
        return this;
    }

    @Override
    public IProductionRegistry addSymbol(Symbol s) {
        return s.isTerminal ?
                addTerminal(s.toString(), s.id) :
                addNonTerminal(s.toString());
    }

    @Override
    public IProductionRegistry addNonTerminal(String lexeme) {
        tempProduction.add(registerNonTerminal(lexeme));
        return this;
    }

    @Override
    public IProductionRegistry addTerminal(String lexeme, int id) {
        if (lexemeToSymbol.containsKey(lexeme)) {
            tempProduction.add(lexemeToSymbol.get(lexeme));
        } else {
            Symbol s = new Symbol(lexeme, true, terminalCount++);
            tempProduction.add(s);
            terminalIdRemap.put(id, s.id);
            lexemeToSymbol.put(lexeme, s);
        }
        return this;
    }

    @Override
    public IProductionRegistry addTerminal(char ch) {
        return addTerminal(String.valueOf(ch), ch);
    }

    @Override
    public void finish() {
        addProduction(tempStart, tempProduction);
        tempStart = null;
        tempProduction = new ArrayList<>();
    }

    private Symbol registerNonTerminal(String lexeme) {
        if (lexemeToSymbol.containsKey(lexeme)) {
            return lexemeToSymbol.get(lexeme);
        }
        Symbol s = new Symbol(lexeme, false, nonTerminalCount++);
        lexemeToSymbol.put(lexeme, s);
        return s;
    }

    private void addProduction(Symbol head, List<Symbol> body) {
        ArrayUtil.resize(symbolToProductions, head.id + 1, ArrayList::new);
        Production g = new Production(productionCount, head, body);
        symbolToProductions.get(head.id).add(g);
        productions.add(g);
        productionCount++;
    }

    @Override
    public List<Production> productionsWithHead(Symbol head) {
        return symbolToProductions.get(head.id);
    }

    @Override
    public Production root() {
        return extendedRoot;
    }

    @Override
    public Symbol symbolOf(String lexeme) {
        return null;
    }

    @Override
    public List<Production> grammar() {
        return productions;
    }

    @Override
    public int symbolsCount() {
        return lexemeToSymbol.size();
    }

    @Override
    public Map<Integer, Integer> getTerminalIdRemap() {
        return terminalIdRemap;
    }

    @Override
    public boolean calcForward(Set<Symbol> result, Production g, int startPos) {
        List<Symbol> symbolsBeta = new ArrayList<>(
                g.production().subList(startPos, g.production().size()));
        if (symbolsBeta.isEmpty()) {
            result.add(DefaultSymbols.EPSILON);
        } else {
            first(result, symbolsBeta);
        }
        return result.remove(DefaultSymbols.EPSILON);
    }

    private void first(Set<Symbol> result, List<Symbol> symbols) {
        for (Symbol s : symbols) {
            first(result, s);
            if (!result.contains(DefaultSymbols.EPSILON)) {
                break;
            }
            result.remove(DefaultSymbols.EPSILON);
        }
    }

    private void first(Set<Symbol> result, Symbol s) {
        if (s.isTerminal) {
            result.add(s);
            return;
        }
        for (Production g : productionsWithHead(s)) {
            for (Symbol symbol : g.production()) {
                first(result, symbol);
                if (!result.contains(DefaultSymbols.EPSILON)) {
                    break;
                }
                result.remove(DefaultSymbols.EPSILON);
            }
        }
    }
}
