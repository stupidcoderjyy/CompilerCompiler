package stupidcoder.generate.outunit;

import stupidcoder.generate.OutUnit;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;

public class ConstOut extends OutUnit {
    public String val;

    public ConstOut(String val) {
        this.val = val;
    }

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) throws Exception {
        writer.write(val);
    }

    @Override
    protected boolean shouldRepeat(int count, BufferedInput input) {
        return count < repeat;
    }
}
