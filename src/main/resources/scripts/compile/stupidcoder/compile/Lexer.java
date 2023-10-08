package stupidcoder.compile;

import stupidcoder.util.input.CompilerInput;
import stupidcoder.compile.common.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.BitClass;
import stupidcoder.compile.common.token.TokenFileEnd;
import stupidcoder.compile.tokens.*;

public class Lexer {
    private final boolean[] accepted;
    private final int[][] goTo;
    private final TokenSupplier[] suppliers;
    public final CompilerInput input;

    public Lexer(CompilerInput input) {
        this.input = input;
        $c{%
            accepted = new boolean[$f[fStatesCount]{"%d"}];
            suppliers = new TokenSupplier[$f[fStatesCount]{"%d"}];
        %, LI2}
        $s[compressUsed]{
            $c{%
                goTo = new int[][]{new int[$f[dataSize]{"%d"}], new int[$f[startSize]{"%d"}], new int[$f[offsetsSize]{"%d"}]};
            %},
            $c{%
                goTo = new int[$f[fStatesCount]{"%d"}][128];
            %}
        , LI2}
        init();
    }

    private void init() {
        $arr[goTo]{"goTo", "int", $f{"%s"}, I2}

        $arr[accepted]{"accepted", "", $f{"%s"}, I2}

        $arr[op]{"suppliers", "TokenSupplier", $f{"(l, i) -> new Token%s().onMatched(l, i)"}, I2}
    }

    public IToken run() throws CompileException{
        input.skip(BitClass.BLANK);
        input.mark();
        if (!input.available()) {
            return TokenFileEnd.INSTANCE;
        }
        $f[fStartState]{"int state = %d;", LI2}
        int lastAccepted = -2;
        int extraLoadedBytes = 0;
        while (input.available()){
            int b = input.read();
            $s[compressUsed]{
                "state = ArrayCompressor.next(state, b, goTo);",
                "state = goTo[state][b];"
            , LI3}
            if (state == 0) {
                extraLoadedBytes++;
                break;
            }
            if (accepted[state]) {
                lastAccepted = state;
                extraLoadedBytes = 0;
            } else {
                extraLoadedBytes++;
            }
        }
        if (lastAccepted < 0 || suppliers[lastAccepted] == null) {
            input.approach('\r', ' ', '\t');
            throw input.errorMarkToForward("unexpected symbol");
        }
        input.retract(extraLoadedBytes);
        input.mark();
        return suppliers[lastAccepted].get(input.capture(), input);
    }

    @FunctionalInterface
    private interface TokenSupplier{
        IToken get(String lexeme, CompilerInput input) throws CompileException;
    }
}
