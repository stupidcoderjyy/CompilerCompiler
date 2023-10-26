package stupidcoder.generate.project.java.importParser;

import stupidcoder.common.token.IToken;
import stupidcoder.common.token.TokenFileEnd;
import stupidcoder.generate.project.java.importParser.tokens.TokenId;
import stupidcoder.generate.project.java.importParser.tokens.TokenSingle;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class Lexer {
    private final boolean[] accepted;
    private final int[][] goTo;
    private final TokenSupplier[] suppliers;

    public Lexer() {
        accepted = new boolean[5];
        suppliers = new TokenSupplier[5];
        goTo = new int[5][128];
        init();
    }

    private void init() {
        goTo[3][36] = 1;
        goTo[3][46] = 1;
        goTo[3][59] = 1;
        goTo[3][42] = 2;
        for(int i = 48 ; i <= 57 ; i ++) {
            goTo[4][i] = 4;
        }
        for(int i = 97 ; i <= 122 ; i ++) {
            goTo[3][i] = 4;
            goTo[4][i] = 4;
        }
        for(int i = 65 ; i <= 90 ; i ++) {
            goTo[3][i] = 4;
            goTo[4][i] = 4;
        }

        accepted[1] = true;
        accepted[2] = true;
        accepted[4] = true;

        TokenSupplier e1 = (l, i) -> new TokenId().onMatched(l, i);
        suppliers[1] = (l, i) -> new TokenSingle().onMatched(l, i);
        suppliers[2] = e1;
        suppliers[4] = e1;
    }

    public IToken run(CompilerInput input) throws CompileException{
        input.skip(BitClass.BLANK);
        input.mark();
        if (!input.available()) {
            return TokenFileEnd.INSTANCE;
        }
        int state = 3;
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
