package stupidcoder.compile.tokens;

import stupidcoder.compile.common.token.IToken;
import stupidcoder.util.input.CompilerInput;
import stupidcoder.util.input.CompileException;

import java.util.HashMap;
import java.util.Map;

public class TokenId implements IToken {
    private static final Map<String, Integer> keyWords = new HashMap<>();
    public String lexeme;

    static {
        $f[keyWords]{"keyWords.put(\"%s\", %d);", LRI2}
    }

    @Override
    public int type() {
        $f[id]{"return keyWords.getOrDefault(lexeme, %d);", LI2}
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