package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.ConstOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserUnitConst implements Parser {

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        ConstOut cst = new ConstOut(readStringArg(input));
        if (checkArgInterval(input)) {
            return cst;
        }
        InternalParsers.ARG_COMMON_OUTPUT_CONFIG.parse(g, input, cst);
        return cst;
    }
}
