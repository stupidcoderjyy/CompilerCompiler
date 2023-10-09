package stupidcoder.generate.generators.java.importParser.tokens;

import stupidcoder.common.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenEnd implements IToken {
    public String lexeme;

    @Override
    public int type() {
        return 128;
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