package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.ComplexOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserUnitComplex implements Parser {

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        ComplexOut cpx = new ComplexOut();
        int b = input.read();
        input.retract();
        if (b == '%') {
            InternalParsers.ARG_COMPLEX_PARAGRAPH.parse(g, input, cpx);
        } else {
            InternalParsers.ARG_COMPLEX_LINE.parse(g, input, cpx);
        }
        if (checkArgInterval(input)) {
            return cpx;
        }
        InternalParsers.ARG_COMMON_OUTPUT_CONFIG.parse(g, input, cpx);
        return cpx;
    }
}
