$head{"$compile.tokens", "IToken", "CompilerInput", "TokenFileEnd", "BitClass", "CompileException"}

public class Lexer {
    private final boolean[] accepted;
    private final int[] goTo, start, offsets;
    private final TokenSupplier[] suppliers;
    public final CompilerInput input;

    public Lexer(CompilerInput input) {
        this.input = input;
        $c{%
            accepted = new boolean[$f[fStatesCount]{"%d"}];
            goTo = new int[$f[dataSize]{"%d"}];
            start = new int[$f[startSize]{"%d"}];
            offsets = new int[$f[offsetsSize]{"%d"}];
            suppliers = new TokenSupplier[$f[fStatesCount]{"%d"}];
        %, LI2}
        init();
    }

    private void init() {
        $arr[goTo]{"goTo", "int", $f{"%s"}, I2}
        $arr[start]{"start", "int", $f{"%s"}, I2}
        $arr[offsets]{"offsets", "int", $f{"%s"}, I2}

        $f[accepted]{"accepted[%d] = true;", RLI2}

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
            state = getNext(state, b);
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

    private int getNext(int arg1, int arg2) {
        if (arg1 < 0 || arg1 >= start.length || start[arg1] < 0) {
            return 0;
        }
        int limit = arg1 == start.length - 1 ? goTo.length : start[arg1 + 1];
        int o = arg2 - offsets[arg1];
        if (o < 0) {
            return 0;
        }
        int pos = start[arg1] + o;
        return pos < limit ? goTo[pos] : 0;
    }

    @FunctionalInterface
    private interface TokenSupplier{
        IToken get(String lexeme, CompilerInput input) throws CompileException;
    }
}
