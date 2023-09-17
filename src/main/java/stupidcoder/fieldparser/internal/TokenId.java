package stupidcoder.fieldparser.internal;

import stupidcoder.common.token.IToken;

public class TokenId implements IToken {
    public String lexeme;

    @Override
    public int type() {
        return 128;
    }

    @Override
    public IToken fromLexeme(String lexeme) {
        TokenId t = new TokenId();
        t.lexeme = lexeme;
        return t;
    }

    @Override
    public String toString() {
        return lexeme;
    }
}
