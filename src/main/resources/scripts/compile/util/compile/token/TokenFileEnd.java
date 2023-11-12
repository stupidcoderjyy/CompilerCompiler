package stupidcoder.util.compile.token;

import stupidcoder.util.input.CompilerInput;

public class TokenFileEnd implements IToken{
    public static final TokenFileEnd INSTANCE = new TokenFileEnd();

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
