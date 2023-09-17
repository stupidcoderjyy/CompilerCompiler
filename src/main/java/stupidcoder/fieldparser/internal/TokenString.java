package stupidcoder.fieldparser.internal;

import stupidcoder.common.token.IToken;

public class TokenString implements IToken {
    public String lexeme;

    @Override
    public int type() {
        return 129;
    }

    @Override
    public IToken fromLexeme(String lexeme) {
        TokenString t = new TokenString();
        t.lexeme = lexeme.substring(1, lexeme.length() - 1);
        return t;
    }

    @Override
    public String toString() {
        return "\"" + lexeme + "\"";
    }
}
