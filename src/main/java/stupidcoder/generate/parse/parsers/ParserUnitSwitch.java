package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.SwitchOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserUnitSwitch implements Parser {
    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        SwitchOut switchOut = new SwitchOut();
        while (true) {
            switch (approachNext(input)) {
                case '$', '"' -> switchOut.units.add(InternalParsers.UNIT.parse(g, input, null));
                default -> InternalParsers.ARG_COMMON_OUTPUT_CONFIG.parse(g, input, switchOut);
            }
            if (checkArgInterval(input)) {
                return switchOut;
            }
        }
    }
}
