package stupidcoder.generate.outunit;

import stupidcoder.generate.OutUnit;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class SwitchOut extends OutUnit {
    public final List<OutUnit> units = new ArrayList<>();

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) throws Exception {
        int i = readInt(srcIn);
        if (i < 0 || i >= units.size()) {
            return;
        }
        units.get(i).writeAll(writer, srcIn);
    }
}
