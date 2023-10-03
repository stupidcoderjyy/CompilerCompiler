$head{"CompilerInput", "CompileException"}

$f[name]{"public class %s implements IToken {", L}
    public String lexeme;

    @Override
    public int type() {
        $f[id]{"return %d;", LI2}
    }

    @Override
    public IToken onMatched(String lexeme, CompilerInput input) throws CompileException {
        return this;
    }


    @Override
    public String toString() {
        return lexeme;
    }
}