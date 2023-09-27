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
        if (checkArgInterval(input)) {
            return rpt;
        }
        if (approachNext(input) != '%') {
            InternalParsers.ARG_COMMON_OUTPUT_CONFIG.parse(g, input, rpt);
            if (checkArgInterval(input)) {
                return rpt;
            }
        }
        while (true) {
            if (approachNext(input) != '%') {
                throw input.errorAtForward("unexpected symbol");
            }
            InternalParsers.ARG_COMMON_OUTPUT_FIELD.parse(g, input, rpt);
            switch (approachNext(input)) {
                case ',' -> input.read();
                case '}' -> {
                    return rpt;
                }
                default -> throw input.errorAtForward("unexpected symbol");
            }
        }
    }
}
