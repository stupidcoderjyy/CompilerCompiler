package stupidcoder.core.tokens;

import stupidcoder.common.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenTerminal implements IToken {
    public static final int SINGLE = 0;
    public static final int EPSILON = 1;
    public static final int NORMAL = 2;
    public String lexeme;
    public int terminalType;
    public char ch;

    @Override
    public int type() {
        return 134;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        if (lexeme.charAt(0) == '@') {
            if (lexeme.charAt(1) == '~') {
                this.terminalType = EPSILON;
                this.lexeme = "ε";
            } else {
                this.terminalType = NORMAL;
                this.lexeme = lexeme.substring(1);
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