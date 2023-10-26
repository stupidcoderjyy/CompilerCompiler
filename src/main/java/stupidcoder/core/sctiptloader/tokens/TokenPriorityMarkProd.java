package stupidcoder.core.sctiptloader.tokens;

import stupidcoder.util.compile.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenPriorityMarkProd implements IToken {
    public int value;

    @Override
    public int type() {
        return 134;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        this.value = Integer.parseInt(lexeme.substring(1));
        return this;
    }

    @Override
    public String toString() {
        return "%" + value;
    }
}