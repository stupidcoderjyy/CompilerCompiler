$head{"Production", "Symbol", "IProperty" ,"PropertyTerminal", "IToken", "TokenFileEnd", "$compile.properties"}
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

import java.util.*;
import java.util.function.Supplier;

public class SyntaxAnalyzer {
    private static final int ACCEPT = 0x10000;
    private static final int SHIFT = 0x20000;
    private static final int REDUCE = 0x30000;

    private final int[][] actions;
    private final int[][] goTo;
    private final int[] terminalRemap;
    private final Production[] productions;
    private final Supplier<IProperty>[] propertySuppliers;

    public SyntaxAnalyzer() {
        $c{%
            this.productions = new Production[$f[prodSize]{"%d"}];
            this.actions = new int[$f[statesCount]{"%d"}][$f[terminalCount]{"%d"}];
            this.goTo = new int[$f[statesCount]{"%d"}][$f[nonTerminalCount]{"%d"}];
            this.terminalRemap = new int[$f[remapSize]{"%d"}];
            this.propertySuppliers = new Supplier[$f[nonTerminalCount]{"%d"}];
        %, I2L}
        initTable();
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

    private void initTable() {
        $f[goTo]{"goTo[%d][%d] = %d;", I2LR}
        $c[actions]{
            $f{"actions[%d][%d] = ", I2} +
            $s{
                "ACCEPT;",
                $f{"SHIFT | %d;"},
                $f{"REDUCE | %d;"}
            , L}
        , R}
        $f[remap]{"terminalRemap[%d] = %d;", I2LR}
        $f[property]{"propertySuppliers[%d] = Property%s::new;", I2LR}
    }

    private void initGrammars() {
        $s[syntax]{
            $f{"Symbol e%d = new Symbol(\"%s\", %s, %d);"},
            $c{
                $f{"productions[%d] = new Production(%d, e%d, List.of"} +
                $r{
                    $f{"e%d"},
                    %first-prefix:"(",
                    %single-prefix:"(",
                    %postfix:", ",
                    %last-postfix:")",
                    %single-postfix:")"
                } +
                $f{"); //%s"}
            }
        , I2LR}
    }
}