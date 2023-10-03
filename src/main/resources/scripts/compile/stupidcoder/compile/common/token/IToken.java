$head{"CompilerInput", "CompileException"}

public interface IToken {
    int type();
    IToken onMatched(String lexeme, CompilerInput input) throws CompileException;
}