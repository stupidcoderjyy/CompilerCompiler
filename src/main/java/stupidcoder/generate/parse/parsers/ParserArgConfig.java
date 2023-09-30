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
                    raw.lineBreaks = v.isEmpty() ? 1 : Integer.parseInt(v);
                }
                case 'R', 'r' -> {
                    String v = readIntString(input);
                    raw.repeat = v.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(v);
                }
                case 'I', 'i' -> {
                    String v = readIntString(input);
                    raw.indents = v.isEmpty() ? 1 :Integer.parseInt(v);
                }
                case '}', ',' -> {
                    input.retract();
                    return raw;
                }
                case ' ', '\r', '\n' -> {}
                default -> {
                    input.retract();
                    throw input.errorAtForward("invalid output unit config flag");
                }
            }
        }
        return raw;
    }
}
