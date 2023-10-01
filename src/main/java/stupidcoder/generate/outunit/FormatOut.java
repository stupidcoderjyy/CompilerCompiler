package stupidcoder.generate.outunit;

import stupidcoder.generate.OutUnit;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormatOut extends OutUnit {
    public static final int INTEGER = 0;
    public static final int STRING = 1;
    public static final int CHAR = 2;
    public final List<Integer> types = new ArrayList<>();
    public String fmt;
    public Object[] args;

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) {
        try {
            for (int i = 0 ; i < types.size() ; i ++) {
                switch (types.get(i)) {
                    case INTEGER -> args[i] = readInt(srcIn);
                    case STRING -> args[i] = readString(srcIn);
                    case CHAR -> args[i] = (char) srcIn.read();
                }
            }
            writer.write(String.format(fmt, args));
        } catch (Exception e) {
            System.err.println("failed to output format string\r\n"
                            + "    format:\"" + fmt + "\", args:"
                            + Arrays.toString(args) + ")");
        }
    }

    @Override
    protected boolean shouldRepeat(int count, BufferedInput input) {
        if (types.size() > 0) {
            return super.shouldRepeat(count, input);
        }
        return count < repeat;
    }
}
