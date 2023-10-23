package stupidcoder.core.tokens;

import stupidcoder.common.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenPriorityMarkTerminal implements IToken {
    public int value;

    @Override
    public int type() {
        return 136;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        this.value = Integer.parseInt(lexeme.substring(1));
        return this;
    }

    @Override
    public String toString() {
        return "$" + value;
    }
}