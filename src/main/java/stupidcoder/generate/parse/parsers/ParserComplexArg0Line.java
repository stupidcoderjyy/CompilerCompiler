package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.ComplexOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserComplexArg0Line implements Parser {

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        ComplexOut cpx = (ComplexOut) raw;
        cpx.newLine();
        while (true) {
            cpx.append(InternalParsers.UNIT.parse(g, input, null));
            input.skip(BitClass.BLANK);
            int b = input.read();
            switch (b) {
                case '+' -> {}
                case ',' -> {
                    input.retract();
                    return cpx;
                }
                default -> {
                    input.retract();
                    throw input.errorAtForward("unexpected symbol");
                }
            }
        }
    }
}
