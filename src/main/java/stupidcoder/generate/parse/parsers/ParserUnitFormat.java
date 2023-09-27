package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.FormatOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserUnitFormat implements Parser{

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        checkNext(input, '"');
        FormatOut fmt = new FormatOut();
        input.mark();
        parseFmt(fmt, input);
        input.mark();
        fmt.fmt = input.capture();
        checkNext(input, '"');
        if (checkArgInterval(input)) {
            return fmt;
        }
        InternalParsers.ARG_COMMON_OUTPUT_CONFIG.parse(g, input, fmt);
        return fmt;
    }

    private void parseFmt(FormatOut fmt, CompilerInput input) throws CompileException {
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case '%' -> {
                    if (!input.available()) {
                        throw input.errorAtForward("illegal format");
                    }
                    b = input.read();
                    switch (b) {
                        case 'd' -> fmt.types.add(FormatOut.INTEGER);
                        case 's' -> fmt.types.add(FormatOut.STRING);
                        case 'c' -> fmt.types.add(FormatOut.CHAR);
                        case 'n' -> fmt.types.add(FormatOut.LB);
                        case '"' -> {
                            input.retract();
                            throw input.errorAtForward("illegal format");
                        }
                        default -> throw input.errorMarkToForward("illegal format");
                    }
                }
                case '"' -> {
                    input.retract();
                    fmt.args = new Object[fmt.types.size()];
                    return;
                }
                case '\r' -> throw input.errorAtForward("missing '\"'");
                default -> {}
            }
        }
    }
}
