package stupidcoder.generate;

import stupidcoder.util.input.IInput;

import java.io.FileWriter;
import java.util.List;

public interface ITransform {
    String id();
    void init(List<String> args);
    void clear();
    void writeOnce(FileWriter writer, IInput src) throws Exception;

    default int readInt(IInput input) {
        return (input.read() << 24) | (input.read() << 16) | (input.read() << 8) | input.read();
    }

    default String readString(IInput input) {
        int l = readInt(input);
        input.markLexemeStart();
        input.skip(l);
        return input.lexeme();
    }
}
