package stupidcoder.core.tokens;

import stupidcoder.common.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenBlockEnd implements IToken {

    @Override
    public int type() {
        return 128;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        return this;
    }


    @Override
    public String toString() {
        return "$$";
    }
}