package stupidcoder.util.input;

import java.io.PrintStream;

public class CompileException extends Exception{
    private final int row;
    private int start, end;
    private String line;
    private final String filePath;

    public CompileException(String msg, int row, String line, String filePath) {
        super(msg);
        this.row = row;
        this.filePath = filePath;
        this.line = line;
    }

    public CompileException setPos(int column) {
        start = column;
        end = column;
        return this;
    }

    public CompileException setPos(int start, int end) {
        this.start = start;
        this.end = end;
        return this;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        s.print(filePath);
        s.println(":" + row + ":" + start + ":" + getMessage());
        s.println("    " + line);
        s.println(" ".repeat(start + 4) + "^".repeat(end - start + 1));
    }
}
