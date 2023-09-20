package stupidcoder.generate.source.field;

import java.util.function.Supplier;

public class IntFieldSource extends FieldSource<Integer>{
    public IntFieldSource(String id, Supplier<Integer> supplier) {
        super(id, supplier);
    }

    @Override
    protected int getSize(Integer val) {
        return 4;
    }

    @Override
    public void writeBytes(Integer val) {
        writeInt(val, 0);
    }
}
