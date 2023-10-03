package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.ArraySetterIIIOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserUnitArraySetterIII implements Parser {

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        ArraySetterIIIOut out = new ArraySetterIIIOut();
        out.arrName = InternalParsers.UNIT.parse(g, input, null);
        if (checkArgInterval(input)) {
            return out;
        }
        InternalParsers.ARG_COMMON_OUTPUT_CONFIG.parse(g, input, out.prefix);
        if (checkArgInterval(input)) {
            return out;
        }
        throw input.errorAtForward("too many args");
    }
}
