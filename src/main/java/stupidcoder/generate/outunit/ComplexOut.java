package stupidcoder.generate.outunit;

import stupidcoder.generate.OutUnit;
import stupidcoder.generate.Source;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ComplexOut extends OutUnit {
    private final List<List<OutUnit>> units = new ArrayList<>();
    private boolean[][] overrideMap;
    private static final int LINEBREAK = 0;
    private static final int INDENT = 1;

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
        for (int i = 0 ; shouldRepeat(i, srcIn) ; i ++) {
            for (int id = 0; id < units.size(); id++) {
                List<OutUnit> lineUnits = this.units.get(id);
                if (lineUnits.isEmpty()) {
                    continue;
                }
                if (!overrideMap[INDENT][id]) {
                    writer.write("    ".repeat(indents));
                }
                for (OutUnit u : lineUnits) {
                    u.writeAll(writer, srcIn);
                }
                if (hasLb && !overrideMap[LINEBREAK][id]) {
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
    protected void initConfig() {
        if (overrideMap != null) {
            return;
        }
        super.initConfig();
        overrideMap = new boolean[2][units.size()];
        for (int i = 0; i < units.size(); i++) {
            List<OutUnit> units = this.units.get(i);
            if (units.isEmpty()) {
                continue;
            }
            OutUnit u = units.get(0);
            overrideMap[INDENT][i] = u.indents >= 0;
            overrideMap[LINEBREAK][i] = u.lineBreaks >= 0;
        }
    }

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) {

    }
}
