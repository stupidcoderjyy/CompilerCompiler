package stupidcoder.core.scriptloader.tokens;

import stupidcoder.core.CompilerGenerator;
import stupidcoder.util.Config;
import stupidcoder.util.compile.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenTerminal implements IToken {
    private static final boolean keyWordEnabled = Config.getBool(CompilerGenerator.KEY_WORD_TOKEN);
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
                    } else if (keyWordEnabled) {
                        this.terminalType = KEY_WORD;
                        this.lexeme = lexeme.substring(2);
                    } else {
                        throw input.errorMarkToForward("keyword token disabled");
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