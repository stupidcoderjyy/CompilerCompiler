package stupidcoder.core.scriptloader;

import stupidcoder.core.scriptloader.tokens.*;
import stupidcoder.util.compile.token.IToken;
import stupidcoder.util.compile.token.TokenFileEnd;
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
        accepted = new boolean[40];
        suppliers = new TokenSupplier[40];
        goTo = new int[40][128];
        init();
    }

    private void init() {
        goTo[12][34] = 13;
        goTo[12][36] = 16;
        goTo[12][37] = 17;
        goTo[12][39] = 19;
        goTo[12][45] = 21;
        goTo[12][58] = 1;
        goTo[12][59] = 1;
        goTo[12][124] = 1;
        goTo[12][64] = 26;
        goTo[13][34] = 3;
        goTo[13][92] = 39;
        goTo[14][110] = 15;
        goTo[15][36] = 9;
        goTo[16][36] = 8;
        goTo[16][101] = 38;
        goTo[16][115] = 29;
        goTo[16][116] = 31;
        goTo[18][116] = 20;
        goTo[19][92] = 24;
        goTo[20][97] = 22;
        goTo[21][62] = 10;
        goTo[22][120] = 25;
        goTo[36][34] = 3;
        goTo[36][92] = 39;
        goTo[23][39] = 7;
        goTo[24][39] = 35;
        goTo[39][92] = 39;
        goTo[26][126] = 7;
        goTo[25][36] = 2;
        goTo[26][36] = 30;
        goTo[30][36] = 7;
        goTo[27][100] = 28;
        goTo[28][36] = 4;
        goTo[35][39] = 7;
        goTo[29][121] = 37;
        goTo[31][111] = 32;
        goTo[32][107] = 33;
        goTo[33][101] = 14;
        goTo[37][110] = 18;
        goTo[38][110] = 27;
        goTo[39][34] = 36;
        for(int i = 0 ; i <= 33 ; i ++) {
            goTo[13][i] = 13;
            goTo[36][i] = 13;
            goTo[39][i] = 13;
        }
        for(int i = 0 ; i <= 38 ; i ++) {
            goTo[24][i] = 23;
        }
        for(int i = 40 ; i <= 127 ; i ++) {
            goTo[24][i] = 23;
        }
        for(int i = 48 ; i <= 57 ; i ++) {
            goTo[5][i] = 5;
            goTo[6][i] = 6;
            goTo[11][i] = 11;
            goTo[17][i] = 5;
            goTo[16][i] = 11;
        }
        for(int i = 0 ; i <= 91 ; i ++) {
            goTo[19][i] = 23;
        }
        for(int i = 97 ; i <= 122 ; i ++) {
            goTo[6][i] = 6;
            goTo[12][i] = 6;
            goTo[26][i] = 34;
            goTo[30][i] = 34;
            goTo[34][i] = 34;
        }
        for(int i = 65 ; i <= 90 ; i ++) {
            goTo[6][i] = 6;
            goTo[12][i] = 6;
            goTo[26][i] = 34;
            goTo[30][i] = 34;
            goTo[34][i] = 34;
        }
        for(int i = 93 ; i <= 127 ; i ++) {
            goTo[13][i] = 13;
            goTo[19][i] = 23;
            goTo[36][i] = 13;
            goTo[39][i] = 13;
        }
        for(int i = 35 ; i <= 91 ; i ++) {
            goTo[13][i] = 13;
            goTo[36][i] = 13;
            goTo[39][i] = 13;
        }

        for(int i = 34 ; i <= 36 ; i ++) {
            accepted[i] = true;
        }
        for(int i = 1 ; i <= 11 ; i ++) {
            accepted[i] = true;
        }

        TokenSupplier e2 = (l, i) -> new TokenString().onMatched(l, i);
        TokenSupplier e6 = (l, i) -> new TokenTerminal().onMatched(l, i);
        suppliers[1] = (l, i) -> new TokenSingle().onMatched(l, i);
        suppliers[2] = (l, i) -> new TokenSyntaxBegin().onMatched(l, i);
        suppliers[3] = e2;
        suppliers[36] = e2;
        suppliers[4] = (l, i) -> new TokenEndHead().onMatched(l, i);
        suppliers[5] = (l, i) -> new TokenPriorityMarkProd().onMatched(l, i);
        suppliers[6] = (l, i) -> new TokenId().onMatched(l, i);
        suppliers[7] = e6;
        suppliers[34] = e6;
        suppliers[35] = e6;
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
