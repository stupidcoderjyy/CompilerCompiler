package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.Source;
import stupidcoder.generate.outunit.ConstOut;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserUnit implements Parser {

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        switch (approachNext(input)) {
            case '$' -> input.read();
            case '\"' -> {
                return new ConstOut(readStringArg(input));
            }
            default -> throw input.errorAtForward("unexpected symbol");
        }
        input.mark(); //1 capture begin (getUnitParser)
        input.mark(); //2 err (getUnitParser)
        Parser parser;
        Source src = Source.EMPTY;
        switch (input.approach('{', '[', '\r')) {
            case '{' -> {
                parser = getUnitParser(g, input);
                input.read(); //0
            }
            case '[' -> {
                parser = getUnitParser(g, input);
                input.read(); //0
                input.mark(); //1 err
                input.mark(); //2 capture begin
                input.mark(); //3 err
                if (input.approach(']', '{', ',') != ']') {
                    throw input.errorMarkToForward("missing ']'");
                }
                input.removeMark(); //2
                input.mark(); //3 capture end
                String srcName = input.capture(); //1
                src = g.getSrc(srcName);
                if (src == null) {
                    input.retract();
                    throw input.errorMarkToForward("unknown source: " + srcName);
                }
                input.removeMark(); //0
                input.read();
                checkNext(input, '{');
            }
            default -> throw input.errorAtForward("unexpected symbol");
        }
        OutUnit unit = parser.parse(g, input, null);
        unit.src = src;
        checkNext(input, '}');
        return unit;
    }

    private Parser getUnitParser(Generator g, CompilerInput input) throws CompileException {
        input.mark(); //3
        String unitType = input.capture(); //1
        Parser parser = g.getUnitParser(unitType);
        if (parser == null) {
            input.retract();
            throw input.errorMarkToForward("unknown unit type: " + unitType);
        }
        input.removeMark(); //0
        return parser;
    }
}
