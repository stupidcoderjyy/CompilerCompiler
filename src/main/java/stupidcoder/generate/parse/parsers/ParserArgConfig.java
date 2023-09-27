package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserArgConfig implements Parser {
    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case 'L', 'l' -> {
                    String v = readIntString(input);
                    if (!v.isEmpty()) {
                        raw.lineBreaks = Integer.parseInt(v);
                    }
                }
                case 'R', 'r' -> {
                    String v = readIntString(input);
                    if (!v.isEmpty()) {
                        raw.repeat = Integer.parseInt(v);
                    }
                }
                case 'I', 'i' -> {
                    String v = readIntString(input);
                    if (!v.isEmpty()) {
                        raw.indents = Integer.parseInt(v);
                    }
                }
                case '}', ',' -> {
                    input.retract();
                    return raw;
                }
                case ' ' -> {}
                default -> {
                    input.retract();
                    throw input.errorAtForward("invalid output unit config flag");
                }
            }
        }
        return raw;
    }
}
