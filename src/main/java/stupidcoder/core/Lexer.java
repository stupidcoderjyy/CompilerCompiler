package stupidcoder.core;

import stupidcoder.common.token.IToken;
import stupidcoder.common.token.TokenFileEnd;
import stupidcoder.core.tokens.*;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class Lexer {
    private final int[][] goTo;
    private final boolean[] accepted;
    private final TokenSupplier[] suppliers;
    public final CompilerInput input;

    public Lexer(CompilerInput input) {
        this.input = input;
        accepted = new boolean[30];
        goTo = new int[30][128];
        suppliers = new TokenSupplier[30];
        init();
    }

    private void init() {
        goTo[11][124] = 1;
        goTo[11][34] = 3;
        goTo[12][36] = 7;
        goTo[16][126] = 6;
        goTo[11][36] = 12;
        goTo[11][37] = 13;
        goTo[15][62] = 9;
        goTo[11][39] = 14;
        goTo[11][45] = 15;
        goTo[11][64] = 16;
        goTo[26][36] = 2;
        goTo[12][115] = 17;
        goTo[12][116] = 18;
        goTo[22][36] = 8;
        goTo[27][39] = 6;
        goTo[18][111] = 19;
        goTo[19][107] = 20;
        goTo[20][101] = 21;
        goTo[21][110] = 22;
        goTo[17][121] = 29;
        goTo[23][116] = 24;
        goTo[24][97] = 25;
        goTo[25][120] = 26;
        goTo[29][110] = 23;
        for (int i = 58 ; i <= 59 ; i ++) {
            goTo[11][i] = 1;
        }
        for (int i = 48 ; i <= 57 ; i ++) {
            goTo[4][i] = 4;
            goTo[13][i] = 4;
            goTo[10][i] = 10;
            goTo[12][i] = 10;
        }
        for (int i = 97 ; i <= 122 ; i ++) {
            goTo[5][i] = 5;
            goTo[11][i] = 5;
            goTo[16][i] = 28;
            goTo[28][i] = 28;
        }
        for (int i = 65 ; i <= 90 ; i ++) {
            goTo[5][i] = 5;
            goTo[11][i] = 5;
            goTo[16][i] = 28;
            goTo[28][i] = 28;
        }
        for (int i = 0 ; i <= 127 ; i ++) {
            goTo[14][i] = 27;
        }

        accepted[1] = true;
        accepted[2] = true;
        accepted[3] = true;
        accepted[4] = true;
        accepted[5] = true;
        accepted[6] = true;
        accepted[7] = true;
        accepted[8] = true;
        accepted[9] = true;
        accepted[10] = true;
        accepted[28] = true;

        TokenSupplier e5 = (l, i) -> new TokenTerminal().onMatched(l, i);
        suppliers[1] = (l, i) -> new TokenSingle().onMatched(l, i);
        suppliers[2] = (l, i) -> new TokenSyntaxBegin().onMatched(l, i);
        suppliers[3] = (l, i) -> new TokenString().onMatched(l, i);
        suppliers[4] = (l, i) -> new TokenPriorityMarkProd().onMatched(l, i);
        suppliers[5] = (l, i) -> new TokenId().onMatched(l, i);
        suppliers[6] = e5;
        suppliers[7] = (l, i) -> new TokenBlockEnd().onMatched(l, i);
        suppliers[8] = (l, i) -> new TokenTokenBegin().onMatched(l, i);
        suppliers[9] = (l, i) -> new TokenPoint().onMatched(l, i);
        suppliers[10] = (l, i) -> new TokenPriorityMarkTerminal().onMatched(l, i);
        suppliers[28] = e5;
    }

    public IToken run() throws CompileException{
        input.skip(BitClass.BLANK);
        input.mark();
        if (!input.available()) {
            return TokenFileEnd.INSTANCE;
        }
        int state = 11;
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
