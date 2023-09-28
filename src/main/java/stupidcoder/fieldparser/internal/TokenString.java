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
        this.lexeme = lexeme.substring(1, lexeme.length() - 1);
        return this;
    }

    @Override
    public String toString() {
        return "\"" + lexeme + "\"";
    }
}
