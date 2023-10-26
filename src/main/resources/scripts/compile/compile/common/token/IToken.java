package stupidcoder.common.token;

import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public interface IToken {
    int type();
    stupidcoder.util.common.token.IToken onMatched(String lexeme, CompilerInput input) throws CompileException;
}