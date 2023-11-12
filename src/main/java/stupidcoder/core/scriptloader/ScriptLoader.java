package stupidcoder.core.scriptloader;

import stupidcoder.core.scriptloader.properties.*;
import stupidcoder.lex.NFARegexParser;
import stupidcoder.syntax.SyntaxLoader;
import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;
import stupidcoder.util.compile.property.PropertyTerminal;
import stupidcoder.util.compile.symbol.Symbol;
import stupidcoder.util.compile.token.IToken;
import stupidcoder.util.compile.token.TokenFileEnd;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

public class ScriptLoader {
    private static final int ACCEPT = 0x10000;
    private static final int SHIFT = 0x20000;
    private static final int REDUCE = 0x30000;

    private final int[][] actions;
    private final int[][] goTo;
    private final int[] terminalRemap;
    private final Production[] productions;
    private final Supplier<IProperty>[] propertySuppliers;
    public final SyntaxLoader syntaxLoader;
    public final NFARegexParser regexParser;
    public final Map<String, Integer> nameToTerminalId = new HashMap<>();
    public final Map<String, Integer> keyWords = new HashMap<>();

    public ScriptLoader(SyntaxLoader syntaxLoader, NFARegexParser regexParser) {
        this.syntaxLoader = syntaxLoader;
        this.regexParser = regexParser;
        this.productions = new Production[25];
        this.terminalRemap = new int[138];
        this.propertySuppliers = new Supplier[15];
        this.goTo = new int[36][15];
        this.actions = new int[36][14];
        initActions();
        initGoTo();
        initOthers();
        initGrammars();
    }

    public void run(Lexer lexer) throws CompileException {
        CompilerInput input = lexer.input;
        input.mark();
        Stack<Integer> states = new Stack<>();
        Stack<IProperty> properties = new Stack<>();
        states.push(0);
        IToken token = lexer.run();
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
                    throw lexer.input.errorAtMark("syntax error");
                case 1:
                    propertySuppliers[0].get().onReduced(productions[0], properties.pop());
                    break LOOP;
                case 2:
                    input.mark();
                    states.push(target);
                    properties.push(new PropertyTerminal(token));
                    token = lexer.run();
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
        goTo[19][8] = 21;
        goTo[0][1] = 1;
        goTo[19][9] = 22;
        goTo[0][2] = 2;
        goTo[0][3] = 4;
        goTo[1][3] = 4;
        goTo[1][2] = 35;
        goTo[23][12] = 30;
        goTo[23][11] = 29;
        goTo[3][4] = 14;
        goTo[3][6] = 15;
        goTo[24][13] = 27;
        goTo[3][7] = 17;
        goTo[32][12] = 25;
        goTo[32][10] = 23;
        goTo[5][5] = 6;
        goTo[5][14] = 8;
        goTo[6][14] = 12;
        goTo[32][9] = 33;
        goTo[14][7] = 17;
        goTo[14][6] = 34;
        goTo[19][12] = 25;
        goTo[19][10] = 23;
    }

    private void initActions() {
        actions[0][2] = SHIFT | 3;
        actions[1][2] = SHIFT | 3;
        actions[0][3] = SHIFT | 5;
        actions[1][3] = SHIFT | 5;
        actions[1][0] = ACCEPT;
        actions[2][0] = REDUCE | 1;
        actions[2][2] = REDUCE | 1;
        actions[2][3] = REDUCE | 1;
        actions[3][6] = SHIFT | 16;
        actions[3][7] = SHIFT | 18;
        actions[4][1] = SHIFT | 13;
        actions[5][6] = SHIFT | 7;
        actions[6][6] = SHIFT | 7;
        actions[6][1] = REDUCE | 5;
        actions[7][12] = SHIFT | 9;
        actions[14][6] = SHIFT | 16;
        actions[8][1] = REDUCE | 22;
        actions[8][6] = REDUCE | 22;
        actions[14][7] = SHIFT | 18;
        actions[9][13] = SHIFT | 10;
        actions[10][5] = SHIFT | 11;
        actions[11][1] = REDUCE | 24;
        actions[11][6] = REDUCE | 24;
        actions[12][1] = REDUCE | 23;
        actions[12][6] = REDUCE | 23;
        actions[13][0] = REDUCE | 3;
        actions[13][2] = REDUCE | 3;
        actions[13][3] = REDUCE | 3;
        actions[14][1] = REDUCE | 4;
        actions[15][1] = REDUCE | 7;
        actions[15][6] = REDUCE | 7;
        actions[15][7] = REDUCE | 7;
        actions[16][4] = REDUCE | 9;
        actions[17][4] = SHIFT | 19;
        actions[18][4] = REDUCE | 10;
        actions[19][6] = SHIFT | 20;
        actions[19][10] = SHIFT | 24;
        actions[20][5] = REDUCE | 18;
        actions[20][6] = REDUCE | 18;
        actions[23][6] = SHIFT | 20;
        actions[23][10] = SHIFT | 24;
        actions[21][5] = SHIFT | 31;
        actions[21][8] = SHIFT | 32;
        actions[22][5] = REDUCE | 11;
        actions[22][8] = REDUCE | 11;
        actions[23][5] = REDUCE | 14;
        actions[23][8] = REDUCE | 14;
        actions[23][9] = SHIFT | 28;
        actions[32][6] = SHIFT | 20;
        actions[24][5] = REDUCE | 20;
        actions[24][6] = REDUCE | 20;
        actions[32][10] = SHIFT | 24;
        actions[24][11] = SHIFT | 26;
        actions[25][5] = REDUCE | 16;
        actions[25][6] = REDUCE | 16;
        actions[26][5] = REDUCE | 21;
        actions[26][6] = REDUCE | 21;
        actions[27][5] = REDUCE | 19;
        actions[27][6] = REDUCE | 19;
        actions[28][5] = REDUCE | 15;
        actions[28][8] = REDUCE | 15;
        actions[29][5] = REDUCE | 13;
        actions[29][8] = REDUCE | 13;
        actions[30][5] = REDUCE | 17;
        actions[30][6] = REDUCE | 17;
        actions[31][1] = REDUCE | 8;
        actions[31][6] = REDUCE | 8;
        actions[31][7] = REDUCE | 8;
        actions[33][5] = REDUCE | 12;
        actions[33][8] = REDUCE | 12;
        actions[34][1] = REDUCE | 6;
        actions[34][6] = REDUCE | 6;
        actions[34][7] = REDUCE | 6;
        actions[35][0] = REDUCE | 2;
        actions[35][2] = REDUCE | 2;
        actions[35][3] = REDUCE | 2;
        for(int i = 8 ; i <= 10 ; i ++) {
            actions[20][i] = REDUCE | 18;
            actions[24][i] = REDUCE | 20;
            actions[25][i] = REDUCE | 16;
            actions[26][i] = REDUCE | 21;
            actions[27][i] = REDUCE | 19;
            actions[30][i] = REDUCE | 17;
        }
    }

    private void initOthers() {
        terminalRemap[128] = 1;
        terminalRemap[129] = 2;
        terminalRemap[130] = 3;
        terminalRemap[131] = 4;
        terminalRemap[132] = 6;
        terminalRemap[133] = 7;
        terminalRemap[134] = 9;
        terminalRemap[135] = 10;
        terminalRemap[136] = 11;
        terminalRemap[137] = 13;
        terminalRemap[58] = 12;
        terminalRemap[59] = 5;
        terminalRemap[124] = 8;
        propertySuppliers[12] = () -> new PropertySymbol(syntaxLoader, this, regexParser);
        propertySuppliers[6] = PropertyProduction::new;
        propertySuppliers[8] = PropertyBody::new;
        propertySuppliers[3] = PropertyContent::new;
        propertySuppliers[7] = () -> new PropertyHead(syntaxLoader);
        propertySuppliers[9] = PropertySlice::new;
        propertySuppliers[0] = PropertyRoot::new;
        propertySuppliers[2] = PropertyBlock::new;
        propertySuppliers[5] = PropertyTokens::new;
        propertySuppliers[10] = PropertySeq::new;
        propertySuppliers[11] = () -> new PropertyPriorityP(syntaxLoader);
        propertySuppliers[1] = PropertyScript::new;
        propertySuppliers[14] = () -> new PropertyToken(regexParser);
        propertySuppliers[13] = PropertyPriorityT::new;
        propertySuppliers[4] = PropertySyntax::new;
    }

    private void initGrammars() {
        Symbol e0 = new Symbol("symbol", false, 12);
        Symbol e1 = new Symbol("string", true, 13);
        Symbol e2 = new Symbol("production", false, 6);
        Symbol e3 = new Symbol("body", false, 8);
        Symbol e4 = new Symbol("content", false, 3);
        Symbol e5 = new Symbol("point", true, 4);
        Symbol e6 = new Symbol("priorityMarkTerminal", true, 11);
        Symbol e7 = new Symbol("head", false, 7);
        Symbol e8 = new Symbol("slice", false, 9);
        Symbol e9 = new Symbol("root", false, 0);
        Symbol e10 = new Symbol("priorityMarkProd", true, 9);
        Symbol e11 = new Symbol("block", false, 2);
        Symbol e12 = new Symbol("tokens", false, 5);
        Symbol e13 = new Symbol("id", true, 6);
        Symbol e14 = new Symbol("tokenBegin", true, 3);
        Symbol e15 = new Symbol("seq", false, 10);
        Symbol e16 = new Symbol("priorityP", false, 11);
        Symbol e17 = new Symbol("terminal", true, 10);
        Symbol e18 = new Symbol("blockEnd", true, 1);
        Symbol e19 = new Symbol("script", false, 1);
        Symbol e20 = new Symbol("token", false, 14);
        Symbol e21 = new Symbol("syntaxBegin", true, 2);
        Symbol e22 = new Symbol("ε", true, -1);
        Symbol e23 = new Symbol("endHead", true, 7);
        Symbol e24 = new Symbol("priorityT", false, 13);
        Symbol e25 = new Symbol("syntax", false, 4);
        Symbol e26 = new Symbol(":", true, 12);
        Symbol e27 = new Symbol(";", true, 5);
        Symbol e28 = new Symbol("|", true, 8);
        productions[0] = new Production(0, e9, List.of(e19)); //root → script
        productions[1] = new Production(1, e19, List.of(e11)); //script → block
        productions[2] = new Production(2, e19, List.of(e19, e11)); //script → script block
        productions[3] = new Production(3, e11, List.of(e4, e18)); //block → content blockEnd
        productions[4] = new Production(4, e4, List.of(e21, e25)); //content → syntaxBegin syntax
        productions[5] = new Production(5, e4, List.of(e14, e12)); //content → tokenBegin tokens
        productions[6] = new Production(6, e25, List.of(e25, e2)); //syntax → syntax production
        productions[7] = new Production(7, e25, List.of(e2)); //syntax → production
        productions[8] = new Production(8, e2, List.of(e7, e5, e3, e27)); //production → head point body ;
        productions[9] = new Production(9, e7, List.of(e13)); //head → id
        productions[10] = new Production(10, e7, List.of(e23)); //head → endHead
        productions[11] = new Production(11, e3, List.of(e8)); //body → slice
        productions[12] = new Production(12, e3, List.of(e3, e28, e8)); //body → body | slice
        productions[13] = new Production(13, e8, List.of(e15, e16)); //slice → seq priorityP
        productions[14] = new Production(14, e16, List.of(e22)); //priorityP → ε
        productions[15] = new Production(15, e16, List.of(e10)); //priorityP → priorityMarkProd
        productions[16] = new Production(16, e15, List.of(e0)); //seq → symbol
        productions[17] = new Production(17, e15, List.of(e15, e0)); //seq → seq symbol
        productions[18] = new Production(18, e0, List.of(e13)); //symbol → id
        productions[19] = new Production(19, e0, List.of(e17, e24)); //symbol → terminal priorityT
        productions[20] = new Production(20, e24, List.of(e22)); //priorityT → ε
        productions[21] = new Production(21, e24, List.of(e6)); //priorityT → priorityMarkTerminal
        productions[22] = new Production(22, e12, List.of(e20)); //tokens → token
        productions[23] = new Production(23, e12, List.of(e12, e20)); //tokens → tokens token
        productions[24] = new Production(24, e20, List.of(e13, e26, e1, e27)); //token → id : string ;
    }
}