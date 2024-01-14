package stupidcoder.compile.tokens;

import stupidcoder.compile.common.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

$f[name]{"public class %s implements IToken {", L}
    public String lexeme;

    @Override
    public int type() {
        $f[id]{"return %d;", LI2}
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