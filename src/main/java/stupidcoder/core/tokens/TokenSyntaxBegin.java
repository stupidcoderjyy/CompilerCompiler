package stupidcoder.core.tokens;

import stupidcoder.common.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenSyntaxBegin implements IToken {

    @Override
    public int type() {
        return 129;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        return this;
    }


    @Override
    public String toString() {
        return "$syntax$";
    }
}