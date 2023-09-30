package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.ComplexOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserComplexArg0Line implements Parser {

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        ComplexOut cpx = (ComplexOut) raw;
        cpx.newLine();
        while (true) {
            cpx.append(InternalParsers.UNIT.parse(g, input, null));
            switch (approachNext(input)) {
                case '+' -> input.read();
                case ',', '}' -> {
                    return cpx;
                }
                default -> throw input.errorAtForward("unexpected symbol");
            }
        }
    }
}
