package stupidcoder.generate.outunit;

import stupidcoder.generate.OutUnit;
import stupidcoder.generate.sources.arr.SourceArrSetter;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;

public class ArraySetterOut extends OutUnit {
    public OutUnit arrName, dataExpr, type, prefix = new ConstOut("");

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) throws Exception {
        if (!(src instanceof SourceArrSetter)) {
            throw new RuntimeException("required: Source1DArraySetter or Source2DArraySetter");
        }
        while (srcIn.available()) {
            switch (srcIn.read()) {
                case 0 -> {
                    prefix.writeAll(writer, srcIn);
                    type.writeContentOnce(writer, srcIn);
                    writer.write(" e" + readInt(srcIn) + " = ");
                    dataExpr.writeAll(writer, srcIn);
                    writer.write(";\r\n");
                }
                case 1 -> {
                    prefix.writeAll(writer, srcIn);
                    writeNormal(writer, srcIn);
                }
                case 2 -> {
                    prefix.writeAll(writer, srcIn);
                    writer.write(String.format(
                            "for(int i = %d ; i <= %d ; i ++) {\r\n",
                            readInt(srcIn),
                            readInt(srcIn)));
                    int size = readInt(srcIn);
                    for (int i = 0 ; i < size ; i ++) {
                        prefix.writeAll(writer, srcIn);
                        writer.write("    ");
                        writeNormal(writer, srcIn);
                    }
                    prefix.writeAll(writer, srcIn);
                    writer.write("}\r\n");
                }
            }
        }
    }

    private void writeNormal(FileWriter writer, BufferedInput srcIn) throws Exception {
        arrName.writeContentOnce(writer, srcIn);
        switch (srcIn.read()) {
            case 0 -> writer.write(String.format("[%d][%d] = ", readInt(srcIn), readInt(srcIn)));
            case 1 -> writer.write(String.format("[%d][i] = ", readInt(srcIn)));
            case 2 -> writer.write("[" + readInt(srcIn) + "] = ");
            case 3 -> writer.write("[i] = ");
        }
        if (srcIn.read() == 0) {
            writer.write(readString(srcIn));
        } else {
            dataExpr.writeAll(writer, srcIn);
        }
        writer.write(";\r\n");
    }
}
