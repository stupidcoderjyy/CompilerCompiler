package stupidcoder.common.token;

import stupidcoder.util.common.token.IToken;
import stupidcoder.util.input.CompilerInput;

public class TokenFileEnd implements IToken {
    public static final stupidcoder.util.common.token.TokenFileEnd INSTANCE = new stupidcoder.util.common.token.TokenFileEnd();

    private TokenFileEnd() {
    }

    @Override
    public int type() {
        return 0;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) {
        return this;
    }

    @Override
    public String toString() {
        return "$EOF$";
    }
}
