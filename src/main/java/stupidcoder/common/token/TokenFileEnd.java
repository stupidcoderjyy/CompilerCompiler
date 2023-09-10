package stupidcoder.common.token;

public class TokenFileEnd implements IToken{
    public static final TokenFileEnd INSTANCE = new TokenFileEnd();

    private TokenFileEnd() {
    }

    @Override
    public int type() {
        return 0;
    }

    @Override
    public IToken fromLexeme(String lexeme) {
        return null;
    }
}
