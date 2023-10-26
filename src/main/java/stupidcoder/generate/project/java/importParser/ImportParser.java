package stupidcoder.generate.project.java.importParser;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;
import stupidcoder.common.token.IToken;
import stupidcoder.common.token.TokenFileEnd;
import stupidcoder.generate.project.java.JClassGen;
import stupidcoder.generate.project.java.importParser.properties.*;
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
    private JClassGen clazz;

    public ImportParser() {
        this.lexer = new Lexer();
        this.productions = new Production[11];
        this.terminalRemap = new int[138];
        this.propertySuppliers = new Supplier[7];
        this.goTo = new int[18][7];
        this.actions = new int[18][14];
        initActions();
        initGoTo();
        initOthers();
        initGrammars();
    }

    public void run(CompilerInput input, JClassGen clazz) throws CompileException {
        this.clazz = clazz;
        input.mark();
        Stack<Integer> states = new Stack<>();
        Stack<IProperty> properties = new Stack<>();
        states.push(0);
        IToken token = lexer.run(input);
        if (token == TokenFileEnd.INSTANCE) {
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
                    input.recover(false);
                    throw input.errorAtMark("syntax error");
                case 1:
                    input.retract(input.column());
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

    private void initGoTo() {
        goTo[0][1] = 1;
        goTo[10][4] = 11;
        goTo[0][2] = 3;
        goTo[2][4] = 16;
        goTo[3][3] = 4;
        goTo[3][5] = 6;
        goTo[3][6] = 7;
        goTo[6][6] = 8;
    }

    private void initActions() {
        actions[15][2] = REDUCE | 10;
        actions[15][5] = REDUCE | 10;
        actions[0][1] = SHIFT | 2;
        actions[0][0] = REDUCE | 3;
        actions[0][3] = REDUCE | 3;
        actions[16][2] = SHIFT | 17;
        actions[1][0] = ACCEPT;
        actions[17][0] = REDUCE | 2;
        actions[17][3] = REDUCE | 2;
        actions[2][4] = SHIFT | 12;
        actions[3][0] = REDUCE | 5;
        actions[3][3] = SHIFT | 5;
        actions[4][0] = REDUCE | 1;
        actions[6][3] = SHIFT | 5;
        actions[5][4] = SHIFT | 9;
        actions[10][4] = SHIFT | 12;
        actions[6][0] = REDUCE | 4;
        actions[7][0] = REDUCE | 6;
        actions[7][3] = REDUCE | 6;
        actions[8][0] = REDUCE | 7;
        actions[8][3] = REDUCE | 7;
        actions[9][5] = SHIFT | 10;
        actions[11][5] = SHIFT | 14;
        actions[11][2] = SHIFT | 13;
        actions[12][2] = REDUCE | 9;
        actions[12][5] = REDUCE | 9;
        actions[16][5] = SHIFT | 14;
        actions[13][0] = REDUCE | 8;
        actions[13][3] = REDUCE | 8;
        actions[14][4] = SHIFT | 15;
        for(int i = 6 ; i <= 13 ; i ++) {
            actions[0][i] = REDUCE | 3;
            actions[1][i] = ACCEPT;
            actions[17][i] = REDUCE | 2;
            actions[3][i] = REDUCE | 5;
            actions[4][i] = REDUCE | 1;
            actions[6][i] = REDUCE | 4;
            actions[7][i] = REDUCE | 6;
            actions[8][i] = REDUCE | 7;
            actions[13][i] = REDUCE | 8;
        }
    }

    private void initOthers() {
        terminalRemap[128] = 1;
        terminalRemap[129] = 3;
        terminalRemap[130] = 4;
        terminalRemap[59] = 2;
        terminalRemap[46] = 5;
        terminalRemap[36] = 6;
        for (int i = 131; i <= 137; i++) {
            terminalRemap[i] = 6;

        }
        propertySuppliers[3] = PropertyImports::new;
        propertySuppliers[6] = () -> new PropertyImport(clazz);
        propertySuppliers[5] = PropertyImportList::new;
        propertySuppliers[2] = PropertyPkg::new;
        propertySuppliers[1] = PropertyHead::new;
        propertySuppliers[4] = PropertyPaths::new;
        propertySuppliers[0] = PropertyRoot::new;
    }

    private void initGrammars() {
        Symbol e0 = new Symbol("imports", false, 3);
        Symbol e1 = new Symbol("import", false, 6);
        Symbol e2 = new Symbol("$import", true, 3);
        Symbol e3 = new Symbol("importList", false, 5);
        Symbol e4 = new Symbol("pkg", false, 2);
        Symbol e5 = new Symbol("$package", true, 1);
        Symbol e6 = new Symbol(".", true, 5);
        Symbol e7 = new Symbol("head", false, 1);
        Symbol e8 = new Symbol("ε", true, -1);
        Symbol e9 = new Symbol("paths", false, 4);
        Symbol e10 = new Symbol("root", false, 0);
        Symbol e11 = new Symbol(";", true, 2);
        Symbol e12 = new Symbol("id", true, 4);
        productions[0] = new Production(0, e10, List.of(e7)); //root → head
        productions[1] = new Production(1, e7, List.of(e4, e0)); //head → pkg imports
        productions[2] = new Production(2, e4, List.of(e5, e9, e11)); //pkg → $package paths ;
        productions[3] = new Production(3, e4, List.of(e8)); //pkg → ε
        productions[4] = new Production(4, e0, List.of(e3)); //imports → importList
        productions[5] = new Production(5, e0, List.of(e8)); //imports → ε
        productions[6] = new Production(6, e3, List.of(e1)); //importList → import
        productions[7] = new Production(7, e3, List.of(e3, e1)); //importList → importList import
        productions[8] = new Production(8, e1, List.of(e2, e12, e6, e9, e11)); //import → $import id . paths ;
        productions[9] = new Production(9, e9, List.of(e12)); //paths → id
        productions[10] = new Production(10, e9, List.of(e9, e6, e12)); //paths → paths . id
    }
}