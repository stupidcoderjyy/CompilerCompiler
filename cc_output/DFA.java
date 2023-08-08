package com.stupidcoder.generated;

public class DFA {
    private final int startState = 4;
    private final ILexerInput input;
    private final int[][] goTo;
    private final boolean[] accepted;
    private final IToken[] tokens;

    public DFA(ILexerInput input) {
        this.input = input;
        input.open();
        goTo = new int[11][128];
        accepted = new boolean[11];
        tokens = new IToken[11];
        init();
    }

    private void init() {
        goTo[3]['_'] = 3;
        goTo[6]['-'] = 5;
        goTo[2]['E'] = 6;
        goTo[2]['X'] = 7;
        goTo[2]['x'] = 7;
        goTo[4]['0'] = 2;
        goTo[2]['.'] = 5;
        goTo[1]['F'] = 10;
        goTo[8]['.'] = 5;
        goTo[8]['E'] = 6;
        for (int i = 'A' ; i <= 'F' ; i ++) {
            goTo[9][i] = 9;
            goTo[7][i] = 9;
        }
        for (int i = 'a' ; i <= 'f' ; i ++) {
            goTo[9][i] = 9;
            goTo[7][i] = 9;
        }
        for (int i = '0' ; i <= '9' ; i ++) {
            goTo[1][i] = 1;
            goTo[3][i] = 3;
            goTo[8][i] = 8;
            goTo[9][i] = 9;
            goTo[5][i] = 1;
            goTo[6][i] = 1;
            goTo[2][i] = 8;
            goTo[7][i] = 9;
        }
        for (int i = '1' ; i <= '9' ; i ++) {
            goTo[4][i] = 8;
        }
        for (int i = 'A' ; i <= 'Z' ; i ++) {
            goTo[3][i] = 3;
            goTo[4][i] = 3;
        }
        for (int i = 'a' ; i <= 'z' ; i ++) {
            goTo[3][i] = 3;
            goTo[4][i] = 3;
        }

        accepted[1] = true;
        accepted[2] = true;
        accepted[3] = true;
        accepted[8] = true;
        accepted[9] = true;
        accepted[10] = true;

        IToken t0 = new TokenDouble();
        IToken t1 = new TokenInteger();
        tokens[1] = t0;
        tokens[2] = t1;
        tokens[3] = new TokenWord();
        tokens[8] = t1;
        tokens[9] = t1;
        tokens[10] = t0;
    }

    public IToken nextToken() {
        input.skipSpaceTabLineBreak();
        input.markLexemeStart();
        int state = startState;
        int lastAccepted = 0;
        int extraLoaded = 0;
        while (input.available()) {
            byte b = input.next();
            state = goTo[state][b];
            if (state == 0) {
                break;
            }
            if (accepted[state]) {
                lastAccepted = state;
                extraLoaded = 0;
            } else {
                extraLoaded++;
            }
        }
        input.retract(extraLoaded);
        if (lastAccepted == 0) {
            return null;
        }
        return tokens[lastAccepted].fromLexeme(input.lexeme());
    }
}