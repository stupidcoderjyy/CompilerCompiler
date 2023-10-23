package stupidcoder.core.tokens;

import stupidcoder.Config;
import stupidcoder.common.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenTerminal implements IToken {
    public static final int SINGLE = 0;
    public static final int EPSILON = 1;
    public static final int NORMAL = 2;
    public static final int KEY_WORD = 3;
    public static final int EOF = 4;
    public String lexeme;
    public int terminalType;
    public char ch;

    @Override
    public int type() {
        return 135;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        if (lexeme.charAt(0) == '@') {
            switch (lexeme.charAt(1)) {
                case '~' -> {
                    this.terminalType = EPSILON;
                    this.lexeme = "ε";
                }
                case '$' -> {
                    if (lexeme.charAt(2) == '$') {
                        this.terminalType = EOF;
                        this.lexeme = lexeme;
                    } else if (Config.getBool(Config.KEY_WORD)) {
                        this.terminalType = KEY_WORD;
                        this.lexeme = lexeme.substring(2);
                    } else {
                        throw input.errorMarkToForward("KEY_WORD disabled");
                    }
                }
                default -> {
                    this.terminalType = NORMAL;
                    this.lexeme = lexeme.substring(1);
                }
            }
        } else {
            this.terminalType = SINGLE;
            this.ch = lexeme.charAt(1);
            this.lexeme = String.valueOf(ch);
        }
        return this;
    }

    @Override
    public String toString() {
        return lexeme;
    }
}