package stupidcoder.generate;

import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class OutUnit {
    public Source src = Source.EMPTY;
    public final Map<String, OutUnitField> fields = new HashMap<>();
    public int lineBreaks = -1;
    public int indents = -1;
    public int repeat = -1;

    public abstract void writeContentOnce(FileWriter writer, BufferedInput srcIn) throws Exception;

    public void writeAll(FileWriter writer, BufferedInput parentSrc) throws Exception{
        initConfig();
        boolean nativeSrc = src != Source.EMPTY;
        if (nativeSrc && src.used) {
            src.reset();
        }
        BufferedInput srcIn = nativeSrc ? new BufferedInput(src) : parentSrc;
        for (int i = 0; shouldRepeat(i, srcIn) ; i++) {
            writer.write("    ".repeat(indents));
            writeContentOnce(writer, srcIn);
            writer.write("\r\n".repeat(lineBreaks));
        }
        if (nativeSrc) {
            srcIn.close();
        }
    }

    protected boolean shouldRepeat(int count, BufferedInput input) {
        if (count >= repeat) {
            return false;
        }
        return input == null || input.available();
    }

    protected final int readInt(BufferedInput input) {
        return (input.read() << 24) | (input.read() << 16) | (input.read() << 8) | (input.read() & 0xFF);
    }

    protected final String readString(BufferedInput input) {
        int count = readInt(input);
        input.mark();
        input.read(count);
        input.mark();
        return input.capture();
    }

    protected void initConfig() {
        lineBreaks = Math.max(0, lineBreaks);
        indents = Math.max(0, indents);
        if (repeat < 0) {
            repeat = 1;
        }
    }
}
