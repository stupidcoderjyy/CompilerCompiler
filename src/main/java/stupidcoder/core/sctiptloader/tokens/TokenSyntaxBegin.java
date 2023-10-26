package stupidcoder.core.sctiptloader.tokens;

import stupidcoder.util.compile.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenSyntaxBegin implements IToken {
    public String lexeme;

    @Override
    public int type() {
        return 129;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        this.lexeme = lexeme;
        return this;
    }

    @Override
    public String toString() {
        return lexeme;
    }
}