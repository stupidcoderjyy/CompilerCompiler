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
            switchOut.units.add(InternalParsers.UNIT.parse(g, input, null));
            if (checkArgInterval(input)) {
                return switchOut;
            }
        }
    }
}
