package stupidcoder.generate.project.java.importParser.tokens;

import stupidcoder.common.token.IToken;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

import java.util.HashMap;
import java.util.Map;

public class TokenId implements IToken {
    private static final Map<String, Integer> keyWords = new HashMap<>();
    public String lexeme;

    static {
        keyWords.put("private", 132);
        keyWords.put("package", 128);
        keyWords.put("protected", 133);
        keyWords.put("public", 131);
        keyWords.put("import", 129);
        keyWords.put("record", 136);
        keyWords.put("final", 137);
        keyWords.put("interface", 135);
        keyWords.put("class", 134);
    }

    @Override
    public int type() {
        return keyWords.getOrDefault(lexeme, 130);
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