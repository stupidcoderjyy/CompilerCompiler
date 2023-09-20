package stupidcoder.generate.transform;

import stupidcoder.generate.ITransform;
import stupidcoder.util.input.IInput;

import java.io.FileWriter;
import java.util.List;

public class ConstStringTransform implements ITransform {
    private String val;

    ConstStringTransform() {
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public void writeOnce(FileWriter writer, IInput src) throws Exception {
        writer.write(val);
    }

    @Override
    public String id() {
        return "const";
    }

    @Override
    public void init(List<String> args) {

    }

    @Override
    public void clear() {
    }
}
