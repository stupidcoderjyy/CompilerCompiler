package stupidcoder.generate;

import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;
import stupidcoder.util.input.IInput;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class TransformComplex extends Transform {
    private static final BitClass WRITE_UNIT_END = BitClass.of('+', ',', '}');
    private final List<WriteUnit> outputs = new ArrayList<>();

    @Override
    public void init(Generator g, WriteUnit unit, CompilerInput input) throws CompileException {
        LOOP:
        while (true) {
            outputs.add(new WriteUnitParser(g).parse(input));
            input.mark();
            switch (input.approach(WRITE_UNIT_END)) {
                case ',' -> {
                    input.read();
                    break LOOP;
                }
                case '+' -> input.read();
                case '}' -> {
                    return;
                }
                case '\r', -1 -> throw input.errorMarkToForward("unclosed arg");
            }
        }
        // arg2
        WriteUnitParser.checkNext(input, '"');
        parseGlobalWriteUnitArg(input);
    }

    private void parseGlobalWriteUnitArg(CompilerInput input) throws CompileException {
        input.mark();
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case '\"' -> {
                    return;
                }
                case 'L', 'l' -> {
                    String s = WriteUnitParser.parseInt(input);
                    if (!s.isEmpty()) {
                        for (WriteUnit o : outputs) {
                            if (o.lineBreak >= 0) {
                                continue;
                            }
                            o.setLineBreak(Integer.parseInt(s));
                        }
                    }
                }
                case 'R', 'r' -> {
                    String s = WriteUnitParser.parseInt(input);
                    if (!s.isEmpty()) {
                        for (WriteUnit o : outputs) {
                            if (o.repeat >= 0) {
                                continue;
                            }
                            o.setRepeat(Integer.parseInt(s));
                        }
                    }
                }
                case 'I', 'i' -> {
                    String s = WriteUnitParser.parseInt(input);
                    if (!s.isEmpty()) {
                        for (WriteUnit o : outputs) {
                            if (o.indent >= 0) {
                                continue;
                            }
                            o.setIndent(Integer.parseInt(s));
                        }
                    }
                }
                default -> throw input.errorAtForward("unknown transform arg:'" + (char)b + "'");
            }
        }
        throw input.errorMarkToForward("unclosed arg");
    }

    @Override
    public void clear() {
        outputs.forEach(o -> o.transform.clear());
    }

    @Override
    public void writeOnce(FileWriter writer, IInput src) {
        for (WriteUnit o : outputs) {
            o.output(writer);
        }
    }
}
