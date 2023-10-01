package stupidcoder.generate.sources;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class SourceFieldString extends SourceField<String> {
    private byte[] utf;

    public SourceFieldString(String id, Supplier<String> supplier) {
        super(id, supplier);
    }

    @Override
    protected int getSize(String val) {
        utf = val.getBytes(StandardCharsets.UTF_8);
        return utf.length + 4;
    }

    @Override
    public void writeBytes(String val) {
        writeInt(utf.length, 0);
        System.arraycopy(utf, 0, data, 4, utf.length);
        utf = null;
    }
}
