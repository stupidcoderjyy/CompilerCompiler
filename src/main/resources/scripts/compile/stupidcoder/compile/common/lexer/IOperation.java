$head{"IToken", "IInput"}

public interface IOperation {
    IToken onMatched(String lexeme, IInput input);
}
