package stupidcoder.generate.generators.java;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public class ParserJClassHead implements Parser {
    private final JClassGen clazz;

    public ParserJClassHead(JClassGen clazz) {
        this.clazz = clazz;
    }

    @Override
    public OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException {
        while (true) {
            String s = readStringArg(input);
            clazz.headOut.imports.add(s);
            if (checkArgInterval(input)) {
                return clazz.headOut;
            }
        }
    }
}
