package stupidcoder.generate.generators.java.importParser;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;
import stupidcoder.common.token.IToken;
import stupidcoder.common.token.TokenFileEnd;
import stupidcoder.generate.generators.java.JClassGen;
import stupidcoder.generate.generators.java.JProjectBuilder;
import stupidcoder.generate.generators.java.importParser.properties.PropertyContents;
import stupidcoder.generate.generators.java.importParser.properties.PropertyImport;
import stupidcoder.generate.generators.java.importParser.properties.PropertyRoot;
import stupidcoder.generate.generators.java.importParser.properties.PropertySeq;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

public class ImportParser {
    private static final int ACCEPT = 0x10000;
    private static final int SHIFT = 0x20000;
    private static final int REDUCE = 0x30000;

    private final int[][] actions;
    private final int[][] goTo;
    private final int[] terminalRemap;
    private final Production[] productions;
    private final Supplier<IProperty>[] propertySuppliers;
    private final Lexer lexer;
    private final JProjectBuilder builder;
    private CompilerInput input;
    private JClassGen clazz;

    public ImportParser(JProjectBuilder builder, Lexer lexer) {
        this.lexer = lexer;
        this.builder = builder;
        this.productions = new Production[6];
        this.terminalRemap = new int[129];
        this.propertySuppliers = new Supplier[4];
        this.goTo = new int[12][4];
        this.actions = new int[12][4];
        initTable();
        initGrammars();
    }

    public void run(CompilerInput input, JClassGen clazz) throws CompileException {
        this.input = input;
        this.clazz = clazz;
        input.skipLine(); // package
        input.mark();
        Stack<Integer> states = new Stack<>();
        Stack<IProperty> properties = new Stack<>();
        states.push(0);
        IToken token = lexer.run(input);
        if (token == TokenFileEnd.INSTANCE) {
            retractToLineStart();
            return;
        }
        LOOP:
        while (true) {
            int s = states.peek();
            int order = actions[s][terminalRemap[token.type()]];
            int type = order >> 16;
            int target = order & 0xFFFF;
            switch (type) {
                case 0:
                    input.errorAtMark("import syntax error").printStackTrace();
                    retractToLineStart();
                    break LOOP;
                case 1:
                    retractToLineStart();
                    propertySuppliers[0].get().onReduced(productions[0], properties.pop());
                    break LOOP;
                case 2:
                    input.mark();
                    states.push(target);
                    properties.push(new PropertyTerminal(token));
                    token = lexer.run(input);
                    break;
                case 3:
                    Production p = productions[target];
                    int size = p.body().size();
                    IProperty[] body = new IProperty[size];
                    for (int i = size - 1 ; i >= 0 ; i --) {
                        Symbol symbol = p.body().get(i);
                        if (symbol.id < 0) {
                            continue; //ε
                        }
                        states.pop();
                        body[i] = properties.pop();
                        if (symbol.isTerminal) {
                            input.removeMark();
                        }
                    }
                    IProperty pHead = propertySuppliers[p.head().id].get();
                    pHead.onReduced(p, body);
                    properties.push(pHead);
                    states.push(goTo[states.peek()][p.head().id]);
                    break;
            }
        }
    }

    private void retractToLineStart() {
        while (true) {
            if (input.retract() == '\r') {
                input.skipLine();
                break;
            }
        }
    }

    private void initTable() {
        goTo[0][1] = 1;
        goTo[0][2] = 3;
        goTo[1][2] = 11;
        goTo[5][3] = 7;
        actions[0][1] = SHIFT | 2;
        actions[1][1] = SHIFT | 2;
        actions[1][0] = ACCEPT;
        actions[2][1] = SHIFT | 4;
        actions[3][0] = REDUCE | 1;
        actions[3][1] = REDUCE | 1;
        actions[4][2] = SHIFT | 5;
        actions[5][1] = SHIFT | 6;
        actions[6][2] = REDUCE | 4;
        actions[6][3] = REDUCE | 4;
        actions[7][2] = SHIFT | 8;
        actions[7][3] = SHIFT | 9;
        actions[8][1] = SHIFT | 10;
        actions[9][0] = REDUCE | 3;
        actions[9][1] = REDUCE | 3;
        actions[10][2] = REDUCE | 5;
        actions[10][3] = REDUCE | 5;
        actions[11][0] = REDUCE | 2;
        actions[11][1] = REDUCE | 2;
        terminalRemap[128] = 1;
        terminalRemap[59] = 3;
        terminalRemap[46] = 2;
        propertySuppliers[2] = () -> new PropertyImport(builder, clazz);
        propertySuppliers[1] = PropertyContents::new;
        propertySuppliers[0] = PropertyRoot::new;
        propertySuppliers[3] = PropertySeq::new;
    }

    private void initGrammars() {
        Symbol e0 = new Symbol("import", false, 2);
        Symbol e1 = new Symbol("contents", false, 1);
        Symbol e2 = new Symbol("root", false, 0);
        Symbol e3 = new Symbol("id", true, 1);
        Symbol e4 = new Symbol(";", true, 3);
        Symbol e5 = new Symbol(".", true, 2);
        Symbol e6 = new Symbol("seq", false, 3);
        productions[0] = new Production(0, e2, List.of(e1)); //root → contents
        productions[1] = new Production(1, e1, List.of(e0)); //contents → import
        productions[2] = new Production(2, e1, List.of(e1, e0)); //contents → contents import
        productions[3] = new Production(3, e0, List.of(e3, e3, e5, e6, e4)); //import → id id . seq ;
        productions[4] = new Production(4, e6, List.of(e3)); //seq → id
        productions[5] = new Production(5, e6, List.of(e6, e5, e3)); //seq → seq . id
    }
}