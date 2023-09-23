$pkg$[-f]{"package %s.%s;"}

$internal_import$[R-f]{"import %s.%s.%s;"}

$external_import$[R-f]{"import %s;"}

public class DFA {
    $start_state$[I1-f]{"private static final int startState = %d;"}
    private final int[][] goTo;
    private final boolean[] accepted;
    private final IOperation[] operations;
    public final IInput input;

    public DFA(IInput input) {
        this.input = input;
        $states_count$[I2-f]{"accepted = new boolean[%d];"}
        $states_count$[I2-f]{"goTo = new int[%d][128];"}
        $states_count$[I2-f]{"operations = new IOperation[%d];"}
        init();
    }

    private void init() {
        $set_goto$[I2-ArraySetter2D]{} //我认为应当分成for源和普通源，不应该合并在一起
        $set_accepted$[RI2-f]{"accepted[%d] = true;"}
        $set_operations_1$[RI2-f]{"IOperation e%d = %s;"}
        $set_operations_2$[RI2-f]{"operations[%d] = %s;"}
    }

    public IToken run() {
        input.markLexemeStart();
        input.skipSpaceTabLineBreak();
        if (!input.available()) {
            return TokenFileEnd.INSTANCE;
        }
        int state = startState;
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
