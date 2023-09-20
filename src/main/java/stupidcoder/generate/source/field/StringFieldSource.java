package stupidcoder.generate.source.field;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class StringFieldSource extends FieldSource<String> {
    public StringFieldSource(String id, Supplier<String> supplier) {
        super(id, supplier);
    }

    @Override
    protected int getSize(String val) {
        data = val.getBytes(StandardCharsets.UTF_8);
        return data.length;
    }

    @Override
    protected byte[] getBytes() {
        return data;
    }

    @Override
    public void writeBytes(String val) {

    }
}
