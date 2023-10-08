package stupidcoder.compile.syntax;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.common.symbol.Symbol;
import stupidcoder.util.ArrayUtil;

import java.util.*;

public class SyntaxLoader {
    private final List<List<Production>> symbolToProductions = new ArrayList<>();
    private final List<Integer> terminalPriority = new ArrayList<>();
    private final List<Integer> productionPriority = new ArrayList<>();
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
            addProduction(root, List.of(tempStart), false);
            extendedRoot = productions.get(0);
        }
        return this;
    }

    public SyntaxLoader add(Symbol s) {
        if (s == DefaultSymbols.EPSILON) {
            tempProduction.add(s);
            lexemeToSymbol.put("ε", s);
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

    public SyntaxLoader setPriority(int value) {
        if (tempProduction.isEmpty()) {
            throw new RuntimeException("empty production");
        }
        Symbol pre = tempProduction.get(tempProduction.size() - 1);
        if (!pre.isTerminal) {
            throw new RuntimeException("cannot set priority to a nonTerminal symbol");
        }
        ArrayUtil.resize(terminalPriority, pre.id + 1,  () -> 0);
        terminalPriority.set(pre.id, value);
        return this;
    }

    public SyntaxLoader addTerminal(char ch) {
        return addTerminal(String.valueOf(ch), ch);
    }

    public void finish() {
        finish0(false);
    }

    public void finish(int priority) {
        finish0(true);
        ArrayUtil.resize(productionPriority, productionCount, () -> 0);
        productionPriority.set(productionCount - 1, priority);
    }

    private void finish0(boolean customPriority) {
        addProduction(tempStart, tempProduction, customPriority);
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

    private void addProduction(Symbol head, List<Symbol> body, boolean customPriority) {
        ArrayUtil.resize(symbolToProductions, head.id + 1, ArrayList::new);
        Production p = new Production(productionCount, head, body);
        symbolToProductions.get(head.id).add(p);
        productions.add(p);
        productionCount++;
        if (customPriority) {
            return;
        }
        int size = body.size();
        for (int i = size - 1 ; i >= 0 ; i --) {
            Symbol s = body.get(i);
            if (s.isTerminal && s != DefaultSymbols.EPSILON) {
                ArrayUtil.resize(productionPriority, p.id() + 1, () -> 0);
                ArrayUtil.resize(terminalPriority, s.id + 1, () -> 0);
                productionPriority.set(p.id(), terminalPriority.get(s.id));
                break;
            }
        }
    }

    public List<Production> productionsWithHead(Symbol head) {
        if (head.isTerminal) {
            throw new RuntimeException("terminal");
        }
        if (head.id >= symbolToProductions.size() || symbolToProductions.get(head.id).isEmpty()) {
            throw new RuntimeException("no productions found, head:" + head);
        }
        return symbolToProductions.get(head.id);
    }

    public Production root() {
        return extendedRoot;
    }

    public List<Production> syntax() {
        return productions;
    }

    public Map<String, Symbol> lexemeToSymbol() {
        return lexemeToSymbol;
    }

    public Map<Integer, Integer> terminalIdRemap() {
        return terminalIdRemap;
    }

    public int terminalSymbolsCount() {
        return terminalCount;
    }

    public int nonTerminalSymbolsCount() {
        return nonTerminalCount;
    }

    public boolean shouldReduce(Production target, Symbol forward) {
        return productionPriority.get(target.id()) >= terminalPriority.get(forward.id);
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
            }
        }
    }
}
