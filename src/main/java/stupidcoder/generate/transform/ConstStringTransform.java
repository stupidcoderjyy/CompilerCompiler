package stupidcoder.generate.transform;

import stupidcoder.generate.ITransform;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.List;

public class ConstStringTransform implements ITransform {
    private String val;

    ConstStringTransform() {
    }

    @Override
    public void writeOnce(FileWriter writer, BufferedInput src) throws Exception {
        writer.write(val);
    }

    @Override
    public String id() {
        return "const";
    }

    @Override
    public void init(List<String> args) {
        if (args.isEmpty()) {
            throw new RuntimeException("missing arg");
        }
        this.val = args.get(0);
    }

    @Override
    public void clear() {
    }
}
