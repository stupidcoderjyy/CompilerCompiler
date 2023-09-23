package stupidcoder.generate;

import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;

class WriteUnit {
    Source src;
    Transform transform;
    final Generator g;
    int indent = -1;
    int repeat = -1;
    int lineBreak = -1;

    WriteUnit(Generator g) {
        this.g = g;
    }

    void setIndent(int indent) {
        this.indent = indent << 2;
    }

    void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    void setLineBreak(int lineBreak) {
        this.lineBreak = lineBreak;
    }

    void output(FileWriter writer) {
        indent = Math.max(0, indent);
        if (repeat < 0) {
            repeat = 1;
        }
        if (lineBreak < 0) {
            lineBreak = 1;
        }
        try (BufferedInput input = new BufferedInput(src)) {
            write(writer, input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write(FileWriter writer, BufferedInput input) throws Exception {
        for (int i = 0 ; i < repeat && input.available(); i ++) {
            writer.write(" ".repeat(indent));
            transform.writeOnce(writer, input);
            writer.write("\r\n".repeat(lineBreak));
        }
    }
}
