package stupidcoder.core.tokens;

import org.apache.commons.text.StringEscapeUtils;
import stupidcoder.common.token.IToken;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class TokenString implements IToken {
    public String lexeme;

    @Override
    public int type() {
        return 136;
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        input.mark();
        BitClass clazz = BitClass.of('\\', '\"', '\r');
        while (true) {
            switch (input.approach(clazz)) {
                case '\\' -> {
                    input.read();
                    if (!input.available()) {
                        throw input.errorMarkToForward("unclosed '\"'");
                    }
                    input.read();
                }
                case '\"' -> {
                    input.mark();
                    this.lexeme = StringEscapeUtils.unescapeJava(input.capture());
                    input.read();
                    return this;
                }
                default -> throw input.errorMarkToForward("unclosed '\"'");
            }
        }
    }


    @Override
    public String toString() {
        return lexeme;
    }
}