package stupidcoder.generate;

import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;
import stupidcoder.util.input.IInput;

import java.io.FileWriter;

public class TransformPlain extends Transform {
    TransformPlain() {
    }

    @Override
    public void writeOnce(FileWriter writer, IInput src) throws Exception {
        while (src.available()) {
            writer.write(src.read());
        }
    }

    @Override
    public void init(Generator g, WriteUnit unit, CompilerInput input) throws CompileException {

    }

    @Override
    public void clear() {

    }
}
