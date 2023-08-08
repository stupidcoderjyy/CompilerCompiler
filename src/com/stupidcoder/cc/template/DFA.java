package com.stupidcoder.cc.template;

import com.stupidcoder.cc.template.tokens.TokenInteger;
import com.stupidcoder.cc.template.tokens.TokenWord;
import com.stupidcoder.cc.util.input.ILexerInput;

public class DFA {
    private final ILexerInput input;
    private final int[][] goTo;
    private final boolean[] accepted;
    private final IToken[] tokens;

    public DFA(ILexerInput input) {
        this.input = input;
        input.open();
        goTo = new int[10][128];
        accepted = new boolean[10];
        tokens = new IToken[10];
        init();
    }

    private void init() {
        //...
        tokens[0] = new TokenWord();
        tokens[1] = new TokenInteger();
    }

    public IToken nextToken() {
        input.skipSpaceTabLineBreak();
        input.markLexemeStart();
        int state = 1;
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
        return tokens[lastAccepted].fromLexeme(input.lexeme());
    }
}
