package stupidcoder.common.token;

public class TokenError implements IToken{
    public static final TokenError INSTANCE = new TokenError();
    private String lexeme;

    private TokenError() {
    }

    @Override
    public int type() {
        return 0;
    }

    @Override
    public IToken fromLexeme(String lexeme) {
        this.lexeme = lexeme;
        return this;
    }

    @Override
    public String toString() {
        return "unknown symbol: \"" + lexeme + "\"";
    }
}
