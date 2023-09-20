package stupidcoder.generate.transform;

import stupidcoder.generate.ITransform;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.List;

public class PlainTransform implements ITransform {
    PlainTransform() {
    }

    @Override
    public void writeOnce(FileWriter writer, BufferedInput src) throws Exception {
        while (true) {
            if (src.available()) {
                src.read();
            } else if (src.hasNext()) {
                writer.write(src.lexeme());
            } else {
                writer.write(src.lexeme());
                break;
            }
        }
    }

    @Override
    public String id() {
        return "plain";
    }

    @Override
    public void init(List<String> args) {

    }

    @Override
    public void clear() {

    }
}
