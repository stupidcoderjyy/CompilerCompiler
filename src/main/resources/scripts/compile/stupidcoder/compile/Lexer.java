$head{"$compile.tokens", "IToken", "CompilerInput", "TokenFileEnd", "BitClass", "CompileException"}

public class Lexer {
    private final int[][] goTo;
    private final boolean[] accepted;
    private final TokenSupplier[] suppliers;
    public final CompilerInput input;

    public Lexer(CompilerInput input) {
        this.input = input;
        $c{%
            accepted = new boolean[$f[fStatesCount]{"%d"}];
            goTo = new int[$f[fStatesCount]{"%d"}][128];
            suppliers = new TokenSupplier[$f[fStatesCount]{"%d"}];
        %, I2L1}
        init();
    }

    private void init() {
        $arrIII[goTo]{"goTo", I2}

        $f[accepted]{"accepted[%d] = true;", RLI2}

        $arrIS[op]{"suppliers", "TokenSupplier", $f{"(l, i) -> new Token%s().onMatched(l, i)"}, I2}
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
            state = goTo[state][b];
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
