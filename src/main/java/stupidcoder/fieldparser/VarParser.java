package stupidcoder.fieldparser;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;
import stupidcoder.common.token.IToken;
import stupidcoder.common.token.TokenFileEnd;
import stupidcoder.fieldparser.internal.DFA;
import stupidcoder.fieldparser.internal.IActions;
import stupidcoder.fieldparser.internal.IProperty;
import stupidcoder.fieldparser.internal.PropertyTerminal;
import stupidcoder.fieldparser.internal.properties.PropertyList;

import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

public class VarParser {
    private static final int ACCEPT = 0x10000;
    private static final int MOVE = 0x20000;
    private static final int REDUCE = 0x30000;

    private final int[][] actions;
    private final int[][] goTo;
    private final int[] terminalRemap;
    private final Production[] productions;
    private final Supplier<IProperty>[] propertySuppliers;

    protected VarParser(IActions iActions) {
        this.productions = new Production[10];
        this.actions = new int[16][8];
        this.goTo = new int[16][6];
        this.terminalRemap = new int[130];
        this.propertySuppliers = new Supplier[16];
        initTable();
        initGrammars();
    }

    private void initTable() {
        goTo[0][1] = 1;
        actions[0][1] = MOVE | 2;
        goTo[0][2] = 3;
        actions[3][0] = REDUCE | 1;
        actions[3][1] = REDUCE | 1;
        actions[2][2] = MOVE | 4;
        goTo[4][3] = 5;
        goTo[4][4] = 6;
        actions[4][4] = MOVE | 7;
        actions[4][5] = MOVE | 8;
        actions[8][6] = REDUCE | 9;
        actions[8][7] = REDUCE | 9;
        actions[8][4] = MOVE | 9;
        goTo[8][5] = 10;
        actions[10][6] = MOVE | 11;
        actions[10][7] = MOVE | 12;
        actions[12][4] = MOVE | 13;
        actions[13][6] = REDUCE | 8;
        actions[13][7] = REDUCE | 8;
        actions[11][3] = REDUCE | 6;
        actions[9][6] = REDUCE | 7;
        actions[9][7] = REDUCE | 7;
        actions[7][3] = REDUCE | 4;
        actions[6][3] = REDUCE | 5;
        actions[5][3] = MOVE | 14;
        actions[14][0] = REDUCE | 3;
        actions[14][1] = REDUCE | 3;
        actions[1][0] = ACCEPT;
        actions[1][1] = MOVE | 2;
        goTo[1][2] = 15;
        actions[15][0] = REDUCE | 2;
        actions[15][1] = REDUCE | 2;
        terminalRemap[128] = 1;
        terminalRemap[129] = 4;
        terminalRemap[58] = 2;
        terminalRemap[59] = 3;
        terminalRemap[91] = 5;
        terminalRemap[44] = 7;
        terminalRemap[93] = 6;
        propertySuppliers[0] = PropertyList::new;
    }

    private void initGrammars() {
        Symbol e0 = new Symbol("$start$", false, 0);
        Symbol e13 = new Symbol("stmts", false, 1);
        Symbol e1 = new Symbol("stmt", false, 2);
        Symbol e4 = new Symbol("val", false, 3);
        Symbol e6 = new Symbol("list", false, 4);
        Symbol e12 = new Symbol("strings", false, 5);

        Symbol e2 = new Symbol("@name", true, 1);
        Symbol e3 = new Symbol(":", true, 2);
        Symbol e5 = new Symbol(";", true, 3);
        Symbol e7 = new Symbol("@string", true, 4);
        Symbol e8 = new Symbol("[", true, 5);
        Symbol e9 = new Symbol(",", true, 7);
        Symbol e10 = new Symbol("]", true, 6);
        Symbol e11 = new Symbol("ε", true, 0);

        productions[0] = new Production(0, e0, List.of(e13));//$start$ → stmts
        productions[1] = new Production(1, e13, List.of(e1)); //stmts → stmt
        productions[2] = new Production(2, e13, List.of(e13, e1)); //stmts → stmts stmt
        productions[3] = new Production(3, e1, List.of(e2, e3, e4, e5)); //stmt → @name : val ;
        productions[4] = new Production(4, e4, List.of(e7)); //val → @string
        productions[5] = new Production(5, e4, List.of(e6)); //val → list
        productions[6] = new Production(6, e6, List.of(e8, e12, e10)); //list → [ strings ]
        productions[7] = new Production(7, e12, List.of(e7)); //strings → @string
        productions[8] = new Production(8, e12, List.of(e12, e9, e7)); //strings → strings , @string
        productions[9] = new Production(9, e12, List.of(e11)); //production → ε
    }

    public void run(DFA dfa) {
        try {
            Stack<Integer> states = new Stack<>();
            Stack<IProperty> properties = new Stack<>();
            states.push(0);
            IToken token = dfa.run();
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
                        return;
                    case 1:
                        propertySuppliers[0].get().onReduced(productions[0], properties.pop());
                        break LOOP;
                    case 2:
                        states.push(target);
                        properties.push(new PropertyTerminal<>(token));
                        token = dfa.run();
                        break;
                    case 3:
                        Production p = productions[target];
                        int size = p.body().size();
                        IProperty[] body = new IProperty[size];
                        for (int i = 0 ; i < size ; i ++) {
                            states.pop();
                            body[i] = properties.pop();
                        }
                        IProperty pHead = propertySuppliers[p.head().id].get();
                        pHead.onReduced(p, body);
                        properties.push(pHead);
                        states.push(goTo[states.peek()][p.head().id]);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
