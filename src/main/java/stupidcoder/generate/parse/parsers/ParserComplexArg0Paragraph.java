package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.ComplexOut;
import stupidcoder.generate.outunit.ConstOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserComplexArg0Paragraph implements Parser {
    private static final BitClass LINE_KEY = BitClass.of('\r', '$', '%');

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        ComplexOut complex = (ComplexOut) raw;
        checkNext(input, '%');
        input.skipLine();
        //新的一行，不断读取
        while (true) {
            if (!parseLine(g, input, complex)) {
                break;
            }
        }
        return complex;
    }

    private boolean parseLine(Generator g, CompilerInput input, ComplexOut cpx) throws CompileException {
        input.skip(BitClass.BLANK);
        cpx.newLine();
        while (true) {
            input.mark();
            switch (input.approach(LINE_KEY)) {
                case '$' -> {
                    input.mark();
                    cpx.append(new ConstOut(input.capture()));
                    cpx.append(InternalParsers.UNIT.parse(g, input, null));
                    input.mark();
                }
                case '\r' -> {
                    input.mark();
                    cpx.append(new ConstOut(input.capture()));
                    input.skipLine();
                    return true;
                }
                case '%' -> {
                    input.read();
                    return false;
                }
                case -1 -> throw input.errorAtForward("unclosed '%'");
            }
        }
    }
}
