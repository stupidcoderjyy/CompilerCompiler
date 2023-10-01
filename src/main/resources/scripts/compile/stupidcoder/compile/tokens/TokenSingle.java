$head{"IToken"}

public class TokenSingle implements IToken {
    private char ch;

    @Override
    public int type() {
        return ch;
    }

    @Override
    public IToken fromLexeme(String lexeme) {
        this.ch = lexeme.charAt(0);
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(ch);
    }
}
