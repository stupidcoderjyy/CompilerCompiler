package stupidcoder.generate.outunit;

import stupidcoder.generate.OutUnit;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FormatOut extends OutUnit {
    public static final int INTEGER = 0;
    public static final int STRING = 1;
    public static final int CHAR = 2;
    public static final int LB = 3;
    public final List<Integer> types = new ArrayList<>();
    public String fmt;
    public Object[] args;

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) throws Exception {
        if (srcIn == null || !srcIn.available()) {
            return;
        }
        for (int i = 0 ; i < types.size() ; i ++) {
            switch (types.get(i)) {
                case INTEGER -> args[i] = readInt(srcIn);
                case STRING -> args[i] = readString(srcIn);
                case CHAR -> args[i] = (char) srcIn.read();
                case LB -> {}
            }
        }
        writer.write(String.format(fmt, args));
    }
}
