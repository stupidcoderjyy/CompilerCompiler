package stupidcoder.generate.outunit;

import stupidcoder.generate.OutUnit;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;

public class ArraySetterIIIOut extends OutUnit {
    public OutUnit arrName, prefix = new ConstOut("");

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) throws Exception {
        while (srcIn.available()) {
            prefix.writeAll(writer, srcIn);
            if (readInt(srcIn) == 0) {
                arrName.writeContentOnce(writer, srcIn);
                writer.write(String.format("[%d][%d] = %d;%n", readInt(srcIn), readInt(srcIn), readInt(srcIn)));
            } else {
                writer.write(String.format("for (int i = %d ; i <= %d ; i ++) {%n", readInt(srcIn), readInt(srcIn)));
                int count = readInt(srcIn);
                for (int i = 0; i < count; i++) {
                    prefix.writeAll(writer, srcIn);
                    writer.write("    ");
                    arrName.writeContentOnce(writer, srcIn);
                    writer.write(String.format("[%d][i] = %d;%n", readInt(srcIn), readInt(srcIn)));
                }
                prefix.writeAll(writer, srcIn);
                writer.write("}\r\n");
            }
        }
    }
}
