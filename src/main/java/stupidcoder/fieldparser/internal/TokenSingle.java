package stupidcoder.fieldparser.internal;

import stupidcoder.common.token.IToken;

public class TokenSingle implements IToken {
    private char ch;

    @Override
    public int type() {
        return ch;
    }

    @Override
    public IToken fromLexeme(String lexeme) {
        TokenSingle t = new TokenSingle();
        t.ch = lexeme.charAt(0);
        return t;
    }

    @Override
    public String toString() {
        return String.valueOf(ch);
    }
}
