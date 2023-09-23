package stupidcoder.generate;

import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;
import stupidcoder.util.input.IInput;

import java.io.FileWriter;

public class TransformConstString extends Transform {
    private String val;

    TransformConstString() {
    }

    @Override
    public void writeOnce(FileWriter writer, IInput src) throws Exception {
        writer.write(val);
    }

    @Override
    public void init(Generator g, WriteUnit unit, CompilerInput input) throws CompileException {
        WriteUnitParser.checkNext(input, '\"');
        input.mark();
        if (input.approach('\"', '}', '\r') != '\"') {
            throw input.errorMarkToForward("unclosed '\"'");
        }
        input.mark();
        val = input.capture();
        input.read();
    }

    @Override
    public void clear() {
        val = null;
    }
}
