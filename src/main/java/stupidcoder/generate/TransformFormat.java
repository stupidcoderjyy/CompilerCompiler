package stupidcoder.generate;

import stupidcoder.util.input.*;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class TransformFormat extends Transform {
    private static final int INTEGER = 0;
    private static final int STRING = 1;
    private static final int CHAR = 2;
    private static final int LB = 3;
    private final List<Integer> types = new ArrayList<>();
    private String fmt;
    private Object[] args;

    TransformFormat() {
    }

    @Override
    public void writeOnce(FileWriter writer, IInput src) throws Exception {
        for (int i = 0 ; i < types.size() ; i ++) {
            switch (types.get(i)) {
                case INTEGER -> args[i] = readInt(src);
                case STRING -> args[i] = readString(src);
                case CHAR -> args[i] = (char) src.read();
                case LB -> {}
            }
        }
        writer.write(String.format(fmt, args));
    }

    @Override
    public void init(Generator g, WriteUnit unit, CompilerInput input) throws CompileException {
        WriteUnitParser.checkNext(input, '\"');
        input.mark();
        if (input.approach('\"', '\r') != '\"') {
            throw input.errorMarkToForward("unclosed '\"'");
        }
        input.mark();
        fmt = input.capture();
        parseFmt(new StringInput(fmt));
        input.read();
    }

    private void parseFmt(IInput input) {
        while (input.available()) {
            int b = input.read();
            if (b != '%') {
                continue;
            }
            if (!input.available()) {
                throw new InputException("illegal format:" + fmt);
            }
            b = input.read();
            switch (b) {
                case 'd' -> types.add(INTEGER);
                case 's' -> types.add(STRING);
                case 'c' -> types.add(CHAR);
                case 'n' -> types.add(LB);
                default -> throw new InputException("illegal format:" + fmt);
            }
        }
        args = new Object[types.size()];
    }

    @Override
    public void clear() {
        types.clear();
    }
}
