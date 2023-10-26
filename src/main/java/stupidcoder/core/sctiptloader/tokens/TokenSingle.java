package stupidcoder.core.sctiptloader.tokens;

import stupidcoder.util.compile.token.IToken;
import stupidcoder.util.input.CompilerInput;

public class TokenSingle implements IToken {
    private char ch;

    @Override
    public int type() {
        return ch;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) {
        this.ch = lexeme.charAt(0);
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(ch);
    }
}
