public interface IToken {
    int type();
    IToken fromLexeme(String lexeme);
}