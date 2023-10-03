package stupidcoder.generate.outunit;

import stupidcoder.generate.OutUnit;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;

public class ArraySetterISOut extends OutUnit {
    public OutUnit arrName, arrType, setterExpr, prefix = new ConstOut("");

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) throws Exception {
        while (srcIn.available()) {
            prefix.writeAll(writer, srcIn);
            if (readInt(srcIn) == 0) {
                arrType.writeContentOnce(writer, srcIn);
                writer.write(" e" + readInt(srcIn) + " = ");
                setterExpr.writeContentOnce(writer, srcIn);
            } else {
                arrName.writeContentOnce(writer, srcIn);
                writer.write("[" + readInt(srcIn) + "] = ");
                if (readInt(srcIn) == 0) {
                    writer.write("e" + readInt(srcIn));
                } else {
                    setterExpr.writeContentOnce(writer, srcIn);
                }
            }
            writer.write("\r\n");
        }
    }
}
