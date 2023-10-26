package stupidcoder.core.sctiptloader.tokens;

import stupidcoder.util.compile.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenString implements IToken {
    public String lexeme;

    @Override
    public int type() {
        return 137;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        this.lexeme = lexeme.substring(1, lexeme.length() - 1);
        return this;
    }

    @Override
    public String toString() {
        return lexeme;
    }
}