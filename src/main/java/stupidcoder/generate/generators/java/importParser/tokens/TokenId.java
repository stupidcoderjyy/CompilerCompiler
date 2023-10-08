package stupidcoder.generate.generators.java.importParser.tokens;

import stupidcoder.common.token.IToken;
import stupidcoder.common.token.TokenFileEnd;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

import java.util.HashSet;
import java.util.Set;

public class TokenId implements IToken {
    private static final Set<String> end = new HashSet<>();

    static {
        end.add("public");
        end.add("private");
        end.add("protected");
        end.add("class");
        end.add("interface");
        end.add("record");
        end.add("final");
        end.add("$");
    }
    public String lexeme;

    @Override
    public int type() {
        return 128;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        this.lexeme = lexeme;
        if (end.contains(lexeme)) {
            return TokenFileEnd.INSTANCE;
        }
        return this;
    }


    @Override
    public String toString() {
        return lexeme;
    }
}