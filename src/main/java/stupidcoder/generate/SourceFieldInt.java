package stupidcoder.generate;

import java.util.function.Supplier;

public class SourceFieldInt extends SourceField<Integer> {
    public SourceFieldInt(String id, Supplier<Integer> supplier) {
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
