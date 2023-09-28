$clazzHead${["IOperation", "IToken", "TokenFileEnd", "IInput"]}

public class DFA {
    private final int[][] goTo;
    private final boolean[] accepted;
    private final IOperation[] operations;
    public final IInput input;

    public DFA(IInput input) {
        this.input = input;
        $c[statesCount]{%
            $f{"accepted = new boolean[%d];"}
            $f{"goTo = new int[%d][128];"}
            $f{"operations = new IOperation[%d];"}
        %, I2L1}
        init();
    }

    private void init() {
        $DfaArraySetter[goTo]{"goTo", I2L1}
        $ArraySetter[op]{"IOperation e%d = %s;", I2}
    }

    public IToken run() {
        input.markLexemeStart();
        input.skipSpaceTabLineBreak();
        if (!input.available()) {
            return TokenFileEnd.INSTANCE;
        }
        $f[startState]{"int state = %d;", L1I2}
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
        if (lastAccepted < 0 || operations[lastAccepted] == null) {
            while (input.available()) {
                int b = input.read();
                if (b == ' ') {
                    input.retract();
                    break;
                }
            }
            return TokenError.INSTANCE.fromLexeme(input.lexeme());
        }
        input.retract(extraLoadedBytes);
        return operations[lastAccepted].onMatched(input.lexeme(), input);
    }
}
