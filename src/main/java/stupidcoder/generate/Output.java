package stupidcoder.generate;

import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;

class Output {
    private Source src;
    private ITransform transform;
    private int indent = 0;
    private int repeat = 1;
    private int lineBreak = 1;

    public void setSrc(Source src) {
        this.src = src;
    }

    public void setTransform(ITransform transform) {
        this.transform = transform;
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
