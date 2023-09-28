package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.ComplexOut;
import stupidcoder.generate.outunit.ConstOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserComplexArg0Paragraph implements Parser {
    private static final BitClass LINE_KEY = BitClass.of('\r', '$', '%');

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        ComplexOut cpx = (ComplexOut) raw;
        checkNext(input, '%');
        input.skipLine();
        LOOP_PARA:
        while (true) {
            input.skip(BitClass.BLANK);
            cpx.newLine();
            LOOP_LINE:
            while (true) {
                input.mark();
                switch (input.approach(LINE_KEY)) {
                    case '$' -> {
                        input.mark();
                        String capture = input.capture();
                        if (!capture.isEmpty()) {
                            cpx.append(new ConstOut(capture));
                        }
                        cpx.append(InternalParsers.UNIT.parse(g, input, null));
                        input.mark();
                    }
                    case '\r' -> {
                        input.mark();
                        String capture = input.capture();
                        if (!capture.isEmpty()) {
                            cpx.append(new ConstOut(capture));
                        }
                        input.skipLine();
                        break LOOP_LINE;
                    }
                    case '%' -> {
                        input.read();
                        break LOOP_PARA;
                    }
                    case -1 -> throw input.errorAtForward("unclosed '%'");
                }
            }
        }
        return cpx;
    }
}
