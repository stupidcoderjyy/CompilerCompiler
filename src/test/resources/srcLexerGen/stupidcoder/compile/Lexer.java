public class DFA {
    private final int[][] goTo;
    private final boolean[] accepted;
    private final IOperation[] operations;
    public final IInput input;

    public DFA(IInput input) {
        this.input = input;
        $c{%
            accepted = new boolean[$f[fStatesCount]{"%d"}];
            goTo = new int[$f[fStatesCount]{"%d"}][128];
            operations = new IOperation[$f[fStatesCount]{"%d"}];
        %, I2L1}
        init();
    }

    private void init() {
        $s[goTo]{
            $f{"goTo[%d][%d] = %d;", LI2},
            $c{%
                for (int i = $f{"%d"} ; i <= $f{"%d"} ; i ++) {
                    $r{$f{"goTo[%d][i] = %d;", I3},
                        %postfix:$f{"%n"},
                        %last-postfix:"",
                        %single-postfix:"",
                    , I0}
                }
            %, I2L}
        , R}

        $f[accepted]{"accepted[%d] = true;", RLI2}

        $s[op]{
            $f{"IOperation %s = %s;", I2L},
            $f{"operations[%d] = %s;", I2L}
        , R}
    }

    public IToken run() {
        input.markLexemeStart();
        input.skipSpaceTabLineBreak();
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
