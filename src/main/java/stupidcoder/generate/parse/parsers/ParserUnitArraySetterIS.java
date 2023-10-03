package stupidcoder.generate.parse.parsers;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.outunit.ArraySetterISOut;
import stupidcoder.generate.outunit.ConstOut;
import stupidcoder.generate.outunit.FormatOut;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserUnitArraySetterIS implements Parser {
    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        ArraySetterISOut out = new ArraySetterISOut();
        parseArg1(g, input, out);
        parseArg2(g, input, out);
        if (parseArg3(g, input, out)) {
            return out;
        }
        InternalParsers.ARG_COMMON_OUTPUT_CONFIG.parse(g, input, out.prefix);
        if (!checkArgInterval(input)) {
            throw input.errorAtForward("too many args");
        }
        return out;
    }

    private void parseArg1(Generator g, CompilerInput input, ArraySetterISOut out) throws CompileException {
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

    private void parseArg2(Generator g, CompilerInput input, ArraySetterISOut out) throws CompileException {
        input.mark();
        OutUnit res = InternalParsers.UNIT.parse(g, input, null);
        input.mark();
        input.mark();
        if (checkArgInterval(input)) {
            throw input.errorAtMark("missing setterExpr");
        }
        input.removeMark();
        if (!(res instanceof ConstOut)) {
            throw input.errorMarkToMark("arg arrName should be ConstOut");
        }
        input.removeMark();
        out.arrType = res;
    }

    private boolean parseArg3(Generator g, CompilerInput input, ArraySetterISOut out) throws CompileException {
        input.mark();
        OutUnit res = InternalParsers.UNIT.parse(g, input, null);
        input.mark();
        if (!(res instanceof FormatOut)) {
            throw input.errorMarkToMark("arg arrName should be ConstOut");
        }
        input.removeMark();
        input.removeMark();
        input.mark();
        out.setterExpr = res;
        input.removeMark();
        return checkArgInterval(input);
    }
}
