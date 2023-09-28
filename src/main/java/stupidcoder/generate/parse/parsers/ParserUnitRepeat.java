package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.RepeatOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserUnitRepeat implements Parser {

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        RepeatOut rpt = new RepeatOut();
        rpt.unit = InternalParsers.UNIT.parse(g, input, rpt);
        while (true) {
            if (approachNext(input) == '%') {
                InternalParsers.ARG_COMMON_OUTPUT_FIELD.parse(g, input, rpt);
            } else {
                InternalParsers.ARG_COMMON_OUTPUT_CONFIG.parse(g, input, rpt);
            }
            if (checkArgInterval(input)) {
                return rpt;
            }
        }
    }
}
