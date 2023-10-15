package stupidcoder.compile;

import stupidcoder.util.input.CompilerInput;
import stupidcoder.compile.common.syntax.IProperty;
import stupidcoder.compile.common.token.IToken;
import stupidcoder.compile.common.symbol.Symbol;
import stupidcoder.compile.common.syntax.PropertyTerminal;
import stupidcoder.compile.common.Production;
import stupidcoder.compile.properties.*;
import stupidcoder.util.input.CompileException;
import stupidcoder.compile.common.token.TokenFileEnd;

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
            this.terminalRemap = new int[$f[remapSize]{"%d"}];
            this.propertySuppliers = new Supplier[$f[nonTerminalCount]{"%d"}];
            $s[compressUsed]{
                $c{%
                    this.goTo = new int[][]{new int[$f[goToSize]{"%d"}], new int[$f[goToStartSize]{"%d"}], new int[$f[goToOffsetsSize]{"%d"}]};
                    this.actions = new int[][]{new int[$f[actionsSize]{"%d"}], new int[$f[actionsStartSize]{"%d"}], new int[$f[actionsOffsetsSize]{"%d"}]};
                %, LI2},
                $c{%
                    this.goTo = new int[$f[statesCount]{"%d"}][$f[nonTerminalCount]{"%d"}];
                    this.actions = new int[$f[statesCount]{"%d"}][$f[terminalCount]{"%d"}];
                %, LI2}
            , L0I0}
        %, I2L}
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
            $s[compressUsed]{
                $c{%
                    int order = ArrayCompressor.next(s, terminalRemap[token.type()], actions);
                %},
                $c{%
                    int order = actions[s][terminalRemap[token.type()]];
                %},
            , LI3}
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
                    $s[compressUsed]{
                        $c{%
                            states.push(ArrayCompressor.next(states.peek(), p.head().id, goTo));
                        %},
                        $c{%
                            states.push(goTo[states.peek()][p.head().id]);
                        %},
                    , LI5}
                    break;
            }
        }
    }

    private void initGoTo() {
        $arr[goTo]{"goTo", "int", $f{"%s"}, I2}
    }

    private void initActions() {
        $arr[actions]{"actions", "int", $f{"%s"}, I2}
    }

    private void initOthers() {
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