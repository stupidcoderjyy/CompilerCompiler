package stupidcoder.fieldparser.internal;

import stupidcoder.common.lexer.IOperation;
import stupidcoder.common.token.IToken;
import stupidcoder.common.token.TokenError;
import stupidcoder.common.token.TokenFileEnd;
import stupidcoder.util.input.IInput;

public class DFA {
    private static final int startState = 4;
    private final int[][] goTo;
    private final boolean[] accepted;
    private final IOperation[] operations;
    public final IInput input;

    public DFA(IInput input) {
        this.input = input;
        accepted = new boolean[6];
        goTo = new int[6][128];
        operations = new IOperation[6];
        init();
    }

    private void init() {
        goTo[3]['_'] = 3;
        goTo[4]['"'] = 5;
        goTo[4][','] = 1;
        goTo[4]['['] = 1;
        goTo[4][']'] = 1;
        goTo[5]['"'] = 2;
        for (int i = '#' ; i <= '' ; i ++) {
            goTo[5][i] = 5;
        }
        for (int i = '0' ; i <= '9' ; i ++) {
            goTo[3][i] = 3;
        }
        for (int i = 'A' ; i <= 'Z' ; i ++) {
            goTo[3][i] = 3;
            goTo[4][i] = 3;
        }
        for (int i = 'a' ; i <= 'z' ; i ++) {
            goTo[3][i] = 3;
            goTo[4][i] = 3;
        }
        for (int i = ':' ; i <= ';' ; i ++) {
            goTo[4][i] = 1;
        }
        for (int i = ' ' ; i <= '!' ; i ++) {
            goTo[5][i] = 5;
        }

        accepted[1] = true;
        accepted[2] = true;
        accepted[3] = true;

        operations[1] = (l, i) -> new TokenSingle().fromLexeme(l);
        operations[2] = (l, i) -> new TokenString().fromLexeme(l);
        operations[3] = (l, i) -> new TokenId().fromLexeme(l);
    }

    public IToken run() {
        input.mark();
        input.skip(' ', '\t', '\r', '\n');
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
            return TokenError.INSTANCE.fromLexeme(input.capture());
        }
        input.retract(extraLoadedBytes);
        return operations[lastAccepted].onMatched(input.capture(), input);
    }
}
