package stupidcoder.generate;

import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;
import stupidcoder.util.input.IInput;

import java.io.FileWriter;

public abstract class Transform {
    public abstract void init(Generator g, WriteUnit unit, CompilerInput input) throws CompileException;
    public abstract void clear();
    public abstract void writeOnce(FileWriter writer, IInput src) throws Exception;

    protected final int readInt(IInput srcIn) {
        return (srcIn.read() << 24) | (srcIn.read() << 16) | (srcIn.read() << 8) | (srcIn.read() & 0xFF);
    }

    protected final String readString(IInput srcIn) {
        int l = readInt(srcIn);
        srcIn.mark();
        srcIn.skip(l);
        return srcIn.capture();
    }

    protected final String parseStringArg(CompilerInput in) throws CompileException {
        in.mark();
        if (in.find('"', '\r', '}') != '\"') {
            throw in.errorMarkToForward("unclosed '\"'");
        }
        in.mark();
        return in.capture();
    }
}
