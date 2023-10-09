package stupidcoder.core;

import stupidcoder.common.token.IToken;
import stupidcoder.common.token.TokenFileEnd;
import stupidcoder.core.tokens.*;
import stupidcoder.util.ArrayCompressor;
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
        accepted = new boolean[31];
        suppliers = new TokenSupplier[31];
        goTo = new int[][]{new int[608], new int[31], new int[31]};
        init();
    }

    private void init() {
        goTo[2][16] = 0;
        goTo[2][4] = 48;
        goTo[2][10] = 48;
        goTo[2][15] = 48;
        goTo[1][5] = 10;
        goTo[2][5] = 65;
        goTo[2][26] = 65;
        goTo[2][29] = 65;
        goTo[1][11] = 78;
        goTo[0][78] = 13;
        goTo[2][11] = 120;
        goTo[1][12] = 79;
        goTo[0][79] = 3;
        goTo[0][81] = 14;
        goTo[2][12] = 34;
        goTo[0][82] = 15;
        goTo[0][84] = 16;
        goTo[0][90] = 17;
        goTo[0][103] = 1;
        goTo[0][104] = 1;
        goTo[0][169] = 1;
        goTo[0][109] = 22;
        goTo[1][13] = 170;
        goTo[0][170] = 2;
        goTo[2][13] = 36;
        goTo[2][14] = 36;
        goTo[2][22] = 36;
        goTo[2][25] = 36;
        goTo[0][171] = 7;
        goTo[1][14] = 171;
        goTo[0][250] = 19;
        goTo[0][251] = 20;
        goTo[1][15] = 252;
        goTo[1][16] = 262;
        goTo[1][17] = 390;
        goTo[0][390] = 9;
        goTo[2][17] = 62;
        goTo[1][18] = 391;
        goTo[0][391] = 6;
        goTo[0][485] = 6;
        goTo[2][18] = 39;
        goTo[1][19] = 392;
        goTo[0][392] = 30;
        goTo[2][19] = 121;
        goTo[1][20] = 393;
        goTo[0][393] = 21;
        goTo[2][20] = 111;
        goTo[1][21] = 394;
        goTo[0][394] = 23;
        goTo[2][21] = 107;
        goTo[0][395] = 26;
        goTo[1][22] = 395;
        goTo[1][23] = 486;
        goTo[0][486] = 24;
        goTo[2][23] = 101;
        goTo[1][24] = 487;
        goTo[0][487] = 25;
        goTo[2][24] = 110;
        goTo[2][30] = 110;
        goTo[0][488] = 8;
        goTo[1][25] = 488;
        goTo[1][26] = 489;
        goTo[1][27] = 547;
        goTo[0][547] = 28;
        goTo[2][27] = 116;
        goTo[1][28] = 548;
        goTo[0][548] = 11;
        goTo[2][28] = 97;
        goTo[1][29] = 549;
        goTo[0][607] = 27;
        goTo[1][30] = 607;
        for(int i = 252 ; i <= 261 ; i ++) {
            goTo[0][i] = 4;
        }
        for(int i = 581 ; i <= 606 ; i ++) {
            goTo[0][i] = 29;
        }
        for(int i = 549 ; i <= 574 ; i ++) {
            goTo[0][i] = 29;
        }
        for(int i = 0 ; i <= 3 ; i ++) {
            goTo[2][i] = -1;
        }
        for(int i = 0 ; i <= 4 ; i ++) {
            goTo[1][i] = 0;
        }
        for(int i = 456 ; i <= 481 ; i ++) {
            goTo[0][i] = 29;
        }
        for(int i = 424 ; i <= 449 ; i ++) {
            goTo[0][i] = 29;
        }
        for(int i = 0 ; i <= 9 ; i ++) {
            goTo[0][i] = 4;
        }
        for(int i = 521 ; i <= 546 ; i ++) {
            goTo[0][i] = 29;
        }
        for(int i = 489 ; i <= 514 ; i ++) {
            goTo[0][i] = 29;
        }
        for(int i = 262 ; i <= 389 ; i ++) {
            goTo[0][i] = 18;
        }
        for(int i = 42 ; i <= 67 ; i ++) {
            goTo[0][i] = 5;
        }
        for(int i = 10 ; i <= 35 ; i ++) {
            goTo[0][i] = 5;
        }
        for(int i = 6 ; i <= 9 ; i ++) {
            goTo[2][i] = -1;
        }
        for(int i = 6 ; i <= 10 ; i ++) {
            goTo[1][i] = 68;
        }
        for(int i = 68 ; i <= 77 ; i ++) {
            goTo[0][i] = 10;
        }
        for(int i = 142 ; i <= 167 ; i ++) {
            goTo[0][i] = 5;
        }
        for(int i = 110 ; i <= 135 ; i ++) {
            goTo[0][i] = 5;
        }
        for(int i = 183 ; i <= 192 ; i ++) {
            goTo[0][i] = 10;
        }

        accepted[29] = true;
        for(int i = 1 ; i <= 10 ; i ++) {
            accepted[i] = true;
        }

        TokenSupplier e5 = (l, i) -> new TokenTerminal().onMatched(l, i);
        suppliers[1] = (l, i) -> new TokenSingle().onMatched(l, i);
        suppliers[2] = (l, i) -> new TokenSyntaxBegin().onMatched(l, i);
        suppliers[3] = (l, i) -> new TokenString().onMatched(l, i);
        suppliers[4] = (l, i) -> new TokenPriorityMarkProd().onMatched(l, i);
        suppliers[5] = (l, i) -> new TokenId().onMatched(l, i);
        suppliers[6] = e5;
        suppliers[29] = e5;
        suppliers[7] = (l, i) -> new TokenBlockEnd().onMatched(l, i);
        suppliers[8] = (l, i) -> new TokenTokenBegin().onMatched(l, i);
        suppliers[9] = (l, i) -> new TokenPoint().onMatched(l, i);
        suppliers[10] = (l, i) -> new TokenPriorityMarkTerminal().onMatched(l, i);
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
            state = ArrayCompressor.next(state, b, goTo);
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
