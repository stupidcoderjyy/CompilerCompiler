package stupidcoder.generate.outunit;

import stupidcoder.generate.OutUnit;
import stupidcoder.generate.OutUnitField;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;

public class RepeatOut extends OutUnit {
    public OutUnit unit;
    public final OutUnitField firstPrefix , prefix , lastPrefix;
    public final OutUnitField firstPostfix , postfix , lastPostfix;
    public final OutUnitField singlePrefix, singlePostFix;

    public RepeatOut() {
        this.firstPrefix = new OutUnitField(this, "first-prefix");
        this.prefix = new OutUnitField(this, "prefix");
        this.lastPrefix = new OutUnitField(this, "last-prefix");
        this.firstPostfix = new OutUnitField(this, "first-postfix");
        this.postfix = new OutUnitField(this, "postfix");
        this.lastPostfix = new OutUnitField(this, "last-postfix");
        this.singlePrefix = new OutUnitField(this, "single-prefix");
        this.singlePostFix = new OutUnitField(this, "single-postfix");
    }

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) throws Exception {
        int count = readInt(srcIn);
        check();
        switch (count) {
            case 0 -> {}
            case 1 -> writeOnce(writer, srcIn, singlePrefix.value, singlePostFix.value);
            default -> {
                writeOnce(writer, srcIn, firstPrefix.value, firstPostfix.value);
                for (int i = 1 ; i < count - 1 ; i ++) {
                    writeOnce(writer, srcIn, prefix.value, postfix.value);
                }
                writeOnce(writer, srcIn, lastPrefix.value, lastPostfix.value);
            }
        }
    }
    
    private void writeOnce(FileWriter writer, BufferedInput srcIn, OutUnit aPrefix, OutUnit aPostfix) throws Exception {
        if (aPrefix != null) {
            aPrefix.writeAll(writer, srcIn);
        }
        unit.writeAll(writer, srcIn);
        if (aPostfix != null) {
            aPostfix.writeAll(writer, srcIn);
        }
    }

    private void check() {
        if (firstPrefix.value == null) {
            firstPrefix.value = prefix.value;
        }
        if (lastPrefix.value == null) {
            lastPrefix.value = prefix.value;
        }
        if (firstPostfix.value == null) {
            firstPostfix.value = postfix.value;
        }
        if (lastPostfix.value == null) {
            lastPostfix.value = postfix.value;
        }
        if (singlePostFix.value == null) {
            singlePostFix.value = postfix.value;
        }
        if (singlePrefix.value == null) {
            singlePrefix.value = prefix.value;
        }
    }
}
