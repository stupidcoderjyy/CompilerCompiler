package stupidcoder.generate.outunit;

import stupidcoder.generate.OutUnit;
import stupidcoder.generate.Source;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ComplexOut extends OutUnit {
    private final List<List<OutUnit>> units = new ArrayList<>();

    public void newLine() {
        units.add(new ArrayList<>());
    }

    public void append(OutUnit unit) {
        units.get(units.size() - 1).add(unit);
    }

    @Override
    public void writeAll(FileWriter writer, BufferedInput parentSrc) throws Exception {
        initConfig();
        boolean nativeSrc = src != Source.EMPTY;
        BufferedInput srcIn = nativeSrc ? new BufferedInput(src) : parentSrc;
        boolean hasLb = lineBreaks > 0;
        for (int i = 0 ; i < repeat ; i ++) {
            for (List<OutUnit> lineUnits : units) {
                if (lineUnits.isEmpty()) {
                    continue;
                }
                writer.write("    ".repeat(indents));
                for (OutUnit u : lineUnits) {
                    u.writeAll(writer, srcIn);
                }
                if (hasLb) {
                    writer.write("\r\n");
                }
            }
            if (hasLb) {
                writer.write("\r\n".repeat(lineBreaks - 1));
            }
        }
        if (nativeSrc) {
            srcIn.close();
            src.close();
        }
    }

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) {

    }
}
