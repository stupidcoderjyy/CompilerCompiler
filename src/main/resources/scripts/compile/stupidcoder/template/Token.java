$f[name]{"public class %s implements IToken {", L}
    public String lexeme;

    @Override
    public int type() {
        $f[id]{"return %d;", LI2}
    }

    @Override
    public IToken fromLexeme(String lexeme) {
        return this;
    }
}