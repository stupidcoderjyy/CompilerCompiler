package stupidcoder.generate;

import stupidcoder.util.ASCII;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class WriteUnitParser {
    private static final BitClass SPACE_AND_LB = BitClass.of('\r', '\n', ' ', '\t');
    private static final BitClass SOURCE_END = BitClass.of('$', '[', '{', '"', '\r');
    private static final BitClass TRANSFORM_END = BitClass.of(']', '{', '\"','\r');
    private WriteUnit unit;
    private CompilerInput input;
    private final Generator g;
    private final Source defaultSrc;

    WriteUnitParser(Generator g) {
        this(g, Source.EMPTY);
    }

    WriteUnitParser(Generator g, Source defaultSrc) {
        this.g = g;
        this.defaultSrc = defaultSrc;
    }

    public WriteUnit parse(CompilerInput input) throws CompileException {
        input.skip(BitClass.BLANK);
        this.input = input;
        unit = new WriteUnit(g);
        unit.src = parseSource();
        parseWriterArg();
        unit.transform = parseTransform();
        return unit;
    }

    /*
       required next: $ " [
     */
    private Source parseSource() throws CompileException {
        switch (input.read()) {
            case '\"', '[' -> {
                input.retract();
                return defaultSrc;
            }
            case '$' -> input.mark();
        }
        if (input.approach(SOURCE_END) == '$') {
            input.mark();
            String name = input.capture();
            Source src = g.getSource(name);
            if (src == null) {
                throw input.errorMarkToForward("source not found:\"" + name + "\"");
            }
            input.read();
            return src;
        }
        throw input.errorMarkToForward("unclosed '$'");
    }

    /*
       required next: [ "
     */
    private void parseWriterArg() throws CompileException {
        switch (input.read()) {
            case '\"' -> {
                input.retract();
                return;
            }
            case '[' -> {}
            default -> throw input.errorAtForward("missing '['");
        }
        input.mark();
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case '-', ']'-> {
                    input.retract();
                    return;
                }
                case 'L', 'l' -> {
                    String s = parseInt(input);
                    if (!s.isEmpty()) {
                        unit.setLineBreak(Integer.parseInt(s));
                    }
                }
                case 'R', 'r' -> {
                    String s = parseInt(input);
                    if (!s.isEmpty()) {
                        unit.setRepeat(Integer.parseInt(s));
                    }
                }
                case 'I', 'i' -> {
                    String s = parseInt(input);
                    if (!s.isEmpty()) {
                        unit.setIndent(Integer.parseInt(s));
                    }
                }
                default -> {
                    input.retract();
                    throw input.errorAtForward("unknown transform arg:'" + (char)b + "'");
                }
            }
        }
    }

    static String parseInt(CompilerInput input) {
        input.mark();
        while (input.available()) {
            if (!ASCII.isDigit(input.read())) {
                input.retract();
                input.mark();
                break;
            }
        }
        return input.capture();
    }

    /*
       required next: - ] "
     */
    private Transform parseTransform() throws CompileException {
        input.mark();
        int b = input.read();
        switch (b) {
            case ']' -> {
                if (input.read() != '\"') {
                    input.retract(2);
                    throw input.errorMarkToForward("missing transform type");
                }
                Transform t = new TransformConstString();
                input.retract();
                t.init(g, unit, input);
                return t;
            }
            case '-' -> {
                input.mark();
                if (input.approach(TRANSFORM_END) != ']') {
                    throw input.errorMarkToForward("unclosed ']'");
                }
                input.mark();
                input.read();
                checkNext(input,'{');
                String name = input.capture();
                Transform t = g.getTransform(name);
                if (t == null) {
                    throw input.errorMarkToForward("unknown transform");
                }
                t.init(g, unit, input);
                checkNext(input, '}');
                return t;
            }
            case '\"' -> {
                Transform t = new TransformConstString();
                input.retract();
                t.init(g, unit, input);
                return t;
            }
            default -> throw input.errorAtMark("unexpected symbol:" + (char)b);
        }
    }


    static void checkNext(CompilerInput input, int required) throws CompileException {
        input.skip(SPACE_AND_LB);
        if (input.available() && input.read() == required) {
            return;
        }
        throw input.errorAtForward("missing '" + (char)required + '"');
    }
}