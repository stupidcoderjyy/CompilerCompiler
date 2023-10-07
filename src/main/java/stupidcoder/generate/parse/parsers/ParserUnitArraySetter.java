package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.ArraySetterOut;
import stupidcoder.generate.outunit.ConstOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserUnitArraySetter implements Parser {
    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        ArraySetterOut out = new ArraySetterOut();
        parseName(g, input, out);
        parseArrType(g, input, out);
        out.dataExpr = InternalParsers.UNIT.parse(g, input, null);
        if (checkArgInterval(input)) {
            return out;
        }
        InternalParsers.ARG_COMMON_OUTPUT_CONFIG.parse(g, input, out.prefix);
        if (!checkArgInterval(input)) {
            throw input.errorAtForward("too many args");
        }
        return out;
    }

    private void parseName(Generator g, CompilerInput input, ArraySetterOut out) throws CompileException {
        input.mark();
        OutUnit res = InternalParsers.UNIT.parse(g, input, null);
        input.mark();
        input.mark();
        if (checkArgInterval(input)) {
            throw input.errorAtMark("missing arrType and setterExpr");
        }
        input.removeMark();
        if (!(res instanceof ConstOut)) {
            throw input.errorMarkToMark("arg arrName should be ConstOut");
        }
        input.removeMark();
        out.arrName = res;
    }

    private void parseArrType(Generator g, CompilerInput input, ArraySetterOut out) throws CompileException {
        input.mark();
        OutUnit res = InternalParsers.UNIT.parse(g, input, null);
        input.mark();
        input.mark();
        if (checkArgInterval(input)) {
            throw input.errorAtMark("missing setterExpr");
        }
        input.removeMark();
        if (!(res instanceof ConstOut)) {
            throw input.errorMarkToMark("arg arrType should be ConstOut");
        }
        input.removeMark();
        out.type = res;
    }
}
