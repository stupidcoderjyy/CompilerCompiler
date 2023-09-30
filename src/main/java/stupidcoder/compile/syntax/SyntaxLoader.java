package stupidcoder.compile.syntax;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.common.symbol.Symbol;
import stupidcoder.util.ArrayUtil;

import java.util.*;

public class SyntaxLoader implements ISyntaxAccess {
    private final List<List<Production>> symbolToProductions = new ArrayList<>();
    private Production extendedRoot;
    private Symbol tempStart;
    private List<Symbol> tempProduction = new ArrayList<>();
    final List<Production> productions = new ArrayList<>();
    final Map<Integer, Integer> terminalIdRemap = new HashMap<>();
    final Map<String, Symbol> lexemeToSymbol = new HashMap<>();
    int productionCount = 0;
    int terminalCount = 1;
    int nonTerminalCount = 1;

    public SyntaxLoader() {
        symbolToProductions.add(new ArrayList<>());
    }

    public SyntaxLoader begin(String lexeme) {
        tempStart = registerNonTerminal(lexeme);
        if (extendedRoot == null) {
            Symbol root = DefaultSymbols.ROOT;
            lexemeToSymbol.put(root.toString(), root);
            addProduction(root, List.of(tempStart));
            extendedRoot = productions.get(0);
        }
        return this;
    }

    public SyntaxLoader add(Symbol s) {
        if (s == DefaultSymbols.EPSILON) {
            tempProduction.add(s);
            return this;
        }
        return s.isTerminal ?
                addTerminal(s.toString(), s.id) :
                addNonTerminal(s.toString());
    }

    public SyntaxLoader addNonTerminal(String lexeme) {
        tempProduction.add(registerNonTerminal(lexeme));
        return this;
    }

    public SyntaxLoader addTerminal(String lexeme, int id) {
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

    public SyntaxLoader addTerminal(char ch) {
        return addTerminal(String.valueOf(ch), ch);
    }

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
    public List<Production> syntax() {
        return productions;
    }

    @Override
    public Map<String, Symbol> lexemeToSymbol() {
        return lexemeToSymbol;
    }

    @Override
    public Map<Integer, Integer> terminalIdRemap() {
        return terminalIdRemap;
    }

    @Override
    public int terminalSymbolsCount() {
        return terminalCount;
    }

    @Override
    public int nonTerminalSymbolsCount() {
        return nonTerminalCount;
    }

    boolean calcForward(Set<Symbol> result, Production g, int point) {
        List<Symbol> symbolsBeta = new ArrayList<>(
                g.body().subList(point + 1, g.body().size()));
        if (symbolsBeta.isEmpty()) {
            return true;
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
            for (Symbol symbol : g.body()) {
                first(result, symbol);
                if (!result.contains(DefaultSymbols.EPSILON)) {
                    break;
                }
                result.remove(DefaultSymbols.EPSILON);
            }
        }
    }
}
