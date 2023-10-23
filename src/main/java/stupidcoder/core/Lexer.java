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
        accepted = new boolean[38];
        suppliers = new TokenSupplier[38];
        goTo = new int[38][128];
        init();
    }

    private void init() {
        goTo[32][36] = 9;
        goTo[35][34] = 34;
        goTo[36][110] = 24;
        goTo[37][110] = 32;
        goTo[12][34] = 15;
        goTo[12][36] = 17;
        goTo[12][37] = 20;
        goTo[12][39] = 22;
        goTo[12][45] = 23;
        goTo[12][58] = 1;
        goTo[12][59] = 1;
        goTo[12][124] = 1;
        goTo[12][64] = 26;
        goTo[13][110] = 14;
        goTo[14][116] = 16;
        goTo[15][34] = 3;
        goTo[15][92] = 35;
        goTo[16][97] = 18;
        goTo[17][36] = 8;
        goTo[17][101] = 36;
        goTo[17][115] = 27;
        goTo[17][116] = 28;
        goTo[18][120] = 19;
        goTo[19][36] = 2;
        goTo[21][39] = 7;
        goTo[34][34] = 3;
        goTo[23][62] = 10;
        goTo[34][92] = 35;
        goTo[26][126] = 7;
        goTo[35][92] = 35;
        goTo[24][100] = 25;
        goTo[25][36] = 4;
        goTo[31][36] = 7;
        goTo[26][36] = 31;
        goTo[27][121] = 13;
        goTo[28][111] = 29;
        goTo[29][107] = 30;
        goTo[30][101] = 37;
        for(int i = 0 ; i <= 33 ; i ++) {
            goTo[15][i] = 15;
            goTo[34][i] = 15;
            goTo[35][i] = 15;
        }
        for(int i = 48 ; i <= 57 ; i ++) {
            goTo[5][i] = 5;
            goTo[6][i] = 6;
            goTo[11][i] = 11;
            goTo[17][i] = 11;
            goTo[20][i] = 5;
        }
        for(int i = 97 ; i <= 122 ; i ++) {
            goTo[6][i] = 6;
            goTo[12][i] = 6;
            goTo[26][i] = 33;
            goTo[31][i] = 33;
            goTo[33][i] = 33;
        }
        for(int i = 65 ; i <= 90 ; i ++) {
            goTo[6][i] = 6;
            goTo[12][i] = 6;
            goTo[26][i] = 33;
            goTo[31][i] = 33;
            goTo[33][i] = 33;
        }
        for(int i = 93 ; i <= 127 ; i ++) {
            goTo[15][i] = 15;
            goTo[34][i] = 15;
            goTo[35][i] = 15;
        }
        for(int i = 35 ; i <= 91 ; i ++) {
            goTo[15][i] = 15;
            goTo[34][i] = 15;
            goTo[35][i] = 15;
        }
        for(int i = 0 ; i <= 127 ; i ++) {
            goTo[22][i] = 21;
        }

        accepted[33] = true;
        accepted[34] = true;
        for(int i = 1 ; i <= 11 ; i ++) {
            accepted[i] = true;
        }

        TokenSupplier e2 = (l, i) -> new TokenString().onMatched(l, i);
        TokenSupplier e6 = (l, i) -> new TokenTerminal().onMatched(l, i);
        suppliers[1] = (l, i) -> new TokenSingle().onMatched(l, i);
        suppliers[2] = (l, i) -> new TokenSyntaxBegin().onMatched(l, i);
        suppliers[3] = e2;
        suppliers[34] = e2;
        suppliers[4] = (l, i) -> new TokenEndHead().onMatched(l, i);
        suppliers[5] = (l, i) -> new TokenPriorityMarkProd().onMatched(l, i);
        suppliers[6] = (l, i) -> new TokenId().onMatched(l, i);
        suppliers[7] = e6;
        suppliers[33] = e6;
        suppliers[8] = (l, i) -> new TokenBlockEnd().onMatched(l, i);
        suppliers[9] = (l, i) -> new TokenTokenBegin().onMatched(l, i);
        suppliers[10] = (l, i) -> new TokenPoint().onMatched(l, i);
        suppliers[11] = (l, i) -> new TokenPriorityMarkTerminal().onMatched(l, i);
    }

    public IToken run() throws CompileException{
        input.skip(BitClass.BLANK);
        input.mark();
        if (!input.available()) {
            return TokenFileEnd.INSTANCE;
        }
        int state = 12;
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
