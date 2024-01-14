package stupidcoder.util.input;

import stupidcoder.util.input.readers.FileByteReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class CompilerInput extends BufferedInput {
    private int row = 0;
    private int column = 0;
    private final String filePath;
    private Deque<Integer> columnSizes;
    private Deque<Integer> rowBegin;
    /*
        [0]row
        [1]column
        [2]标记所在的上一行的columnSize
        [3]rowBegin
        [4]forward
     */
    private Deque<int[]> markData;
    private int[] tempData;

    public CompilerInput(IByteReader reader, String filePath) {
        super(reader);
        this.filePath = filePath;
    }

    public CompilerInput(IByteReader reader, String filePath, int bufSize) {
        super(reader, bufSize);
        this.filePath = filePath;
    }

    public static CompilerInput fromFile(String filePath, int bufSize) {
        try {
            return new CompilerInput(new FileByteReader(filePath), filePath, bufSize);
        } catch (FileNotFoundException e) {
            throw new InputException(e.toString());
        }
    }

    public static CompilerInput fromFile(String filePath) {
        try {
            return new CompilerInput(new FileByteReader(filePath), filePath);
        } catch (FileNotFoundException e) {
            throw new InputException(e.toString());
        }
    }

    public static CompilerInput fromResource(String filePath, int bufSize) {
        try {
            InputStream stream = CompilerInput.class.getResourceAsStream(filePath);
            if (stream == null) {
                throw new FileNotFoundException(filePath);
            }
            return new CompilerInput(new FileByteReader(stream), filePath, bufSize);
        } catch (FileNotFoundException e) {
            throw new InputException(e.toString());
        }
    }

    public static CompilerInput fromResource(String filePath) {
        try {
            InputStream stream = CompilerInput.class.getResourceAsStream(filePath);
            if (stream == null) {
                throw new FileNotFoundException(filePath);
            }
            return new CompilerInput(new FileByteReader(stream), filePath);
        } catch (FileNotFoundException e) {
            throw new InputException(e.toString());
        }
    }

    @Override
    protected void init() {
        markData = new ArrayDeque<>();
        rowBegin = new ArrayDeque<>();
        columnSizes = new ArrayDeque<>();
        rowBegin.addFirst(0);
        super.init();
    }

    /**
     * 获取当前行的内容
     * @return 当前行的全部内容
     */
    public final String getFullLine() {
        checkOpen();
        int start = rowBegin.isEmpty() ? 0 : rowBegin.getFirst();
        mark();
        approach('\r');
        String res = capture(forward, start);
        recover();
        return res;
    }

    @Override
    public int retract() {
        if (!markData.isEmpty() && markData.getFirst()[4] == forward) {
            markData.removeFirst();
        }
        int b = super.retract();
        switch (b) {
            case '\r' -> {}
            case '\n' -> {
                row--;
                column = columnSizes.removeFirst();
                rowBegin.removeFirst();
            }
            default -> column--;
        }
        return b;
    }

    @Override
    public int read() {
        int b = super.read();
        //所有和行有关的操作都以'\n'为准，'\r'属于上一行但不计入column
        switch (b) {
            case '\n' -> {
                row++;
                rowBegin.addFirst(forward);
                columnSizes.addFirst(column);
                column = 0;
            }
            case '\r' -> {}
            default -> column++;
        }
        return b;
    }

    @Override
    protected void fillA() {
        super.fillA();
        if (fillCount == 1) {
            return;
        }
        while (!rowBegin.isEmpty() && rowBegin.getLast() < bufEndA) {
            rowBegin.removeLast();
            if (columnSizes.isEmpty()) {
                break;
            }
            columnSizes.removeLast();
        }
        while (!markData.isEmpty() && markData.getLast()[4] < bufEndA) {
            markData.removeLast();
        }
    }

    @Override
    protected void fillB() {
        super.fillB();
        while (!rowBegin.isEmpty() && rowBegin.getLast() >= bufEndA) {
            rowBegin.removeLast();
            columnSizes.removeLast();
        }
        while (!markData.isEmpty() && markData.getLast()[4] >= bufEndA) {
            markData.removeLast();
        }
    }

    /**
     * 标记并保存输入系统的全部信息
     */
    @Override
    public void mark() {
        marks.addFirst(forward);
        markData.addFirst(getData());
    }

    private int[] getData() {
        return new int[]{
                row,
                Math.max(0, column),
                columnSizes.isEmpty() ? -1 : columnSizes.getFirst(),
                rowBegin.getFirst(),
                forward
        };
    }

    /**
     * 移除上一次保存的信息
     */
    @Override
    public void removeMark() {
        marks.pollFirst();
        markData.removeFirst();
    }

    /**
     * 将输入系统恢复到上一次保存的状态
     * @param consume 是否移除标记，移除传入true
     */
    @Override
    public void recover(boolean consume) {
        super.recover(consume);
        if (!markData.isEmpty()) {
            int[] data = consume ? markData.pollFirst() : markData.getFirst();
            row = data[0];
            column = data[1];
            int cs = data[2];
            if (cs > 0) {
                while (columnSizes.getFirst() != cs) {
                    columnSizes.removeFirst();
                }
            } else {
                columnSizes.clear();
            }
            int rb = data[3];
            while (rowBegin.getFirst() != rb) {
                rowBegin.removeFirst();
            }
        }
    }

    /**
     * 得到一个编译错误异常，错误指向最近一个标记，如果没有标记则指向forward
     * @param msg 错误提示
     * @return 得到的异常
     */
    public CompileException errorAtMark(String msg) {
        if (marks.isEmpty()) {
            return errorAtForward(msg);
        }
        return pointError(msg, popMark()[1]);
    }

    /**
     * 得到一个编译错误异常，错误指向下一个输入字符
     * @param msg 错误提示
     * @return 得到的异常
     */
    public CompileException errorAtForward(String msg) {
        return pointError(msg, column);
    }

    /**
     * 得到一个编译错误异常，错误指向最近两个标记之间的内容。两个标记不在同一行会出现奇怪的情况
     * @param msg 错误提示
     * @return 得到的异常
     */
    public CompileException errorMarkToMark(String msg) {
        return switch (marks.size()) {
            case 0 -> errorAtForward(msg);
            case 1 -> errorMarkToForward(msg);
            default -> rangedError(msg, popMark()[1], popMark()[1]);
        };
    }

    private int[] popMark() {
        int[] data = markData.getFirst();
        recover(true);
        return data;
    }

    /**
     * 得到一个编译错误异常，错误指向最近的标记和当前位置之间的内容。当前位置和标记不在同一行会出现奇怪的情况
     * @param msg 错误提示
     * @return 得到的异常
     */
    public CompileException errorMarkToForward(String msg) {
        if (marks.isEmpty()) {
            return errorAtForward(msg);
        }
        return rangedError(msg, column, popMark()[1]);
    }


    /**
     * 跳过本行剩余内容
     */
    public final void skipLine() {
        find('\n');
    }

    private CompileException pointError(String msg, int pos) {
        return new CompileException(msg, row, getFullLine(), filePath).setPos(pos);
    }

    private CompileException rangedError(String msg, int end, int start) {
        return new CompileException(msg, row, getFullLine(), filePath)
                .setPos(start, Math.max(start, end));
    }
}
