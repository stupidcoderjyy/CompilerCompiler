package stupidcoder.generate.transform;

import stupidcoder.generate.ITransform;
import stupidcoder.util.input.IInput;
import stupidcoder.util.input.InputException;
import stupidcoder.util.input.StringInput;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FormatTransform implements ITransform {
    private static final int INTEGER = 0;
    private static final int STRING = 1;
    private static final int CHAR = 2;
    private static final int LB = 3;
    private final List<Integer> types = new ArrayList<>();
    private String fmt;

    FormatTransform() {
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
    }

    @Override
    public void writeOnce(FileWriter writer, IInput src) throws Exception {
        Object[] args = new Object[types.size()];
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
    public String id() {
        return "format";
    }

    @Override
    public void init(List<String> args) {
        this.fmt = args.get(0);
        parseFmt(new StringInput(fmt));
    }

    @Override
    public void clear() {
        types.clear();
    }
}
