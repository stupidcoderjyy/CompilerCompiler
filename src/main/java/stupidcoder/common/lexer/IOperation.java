package stupidcoder.common.lexer;

import stupidcoder.common.token.IToken;
import stupidcoder.util.input.IInput;

public interface IOperation {
    IToken onMatched(String lexeme, IInput input);
}
