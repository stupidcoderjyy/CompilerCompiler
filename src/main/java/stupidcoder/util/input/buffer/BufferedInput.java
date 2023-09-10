package stupidcoder.util.input.buffer;

import stupidcoder.util.input.ILexerInput;
import stupidcoder.util.input.buffer.readers.ConsoleByteReader;
import stupidcoder.util.input.buffer.readers.FileByteReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class BufferedInput implements ILexerInput {
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final int MAX_BUFFER_SIZE = 4096;
    private static final byte INPUT_END = -1;
    private final int bufEndA, bufEndB;
    private final int maxLexemeLen;
    private final IByteReader reader;
    private byte[] buffer;
    private int forward;
    private int lexemeStart;
    private int fillCount;

    public BufferedInput(IByteReader reader, int bufSize) {
        if (bufSize <= 0 || bufSize > MAX_BUFFER_SIZE) {
            throw new IllegalArgumentException("invalid size:" + bufSize + ", required:(0, 4096]");
        }
        if (reader == null) {
            throw new NullPointerException("null reader");
        }
        this.buffer = new byte[bufSize * 2];
        this.forward = 0;
        this.lexemeStart = 0;
        this.reader = reader;
        this.bufEndA = bufSize;
        this.bufEndB = bufSize * 2;
        this.maxLexemeLen = bufSize;
        fill(0);
    }

    public BufferedInput(IByteReader reader) {
        this(reader, DEFAULT_BUFFER_SIZE);
    }

    public static BufferedInput fromFile(String filePath, int bufSize) {
        try {
            return new BufferedInput(new FileByteReader(filePath), bufSize);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedInput fromFile(String filePath) {
        return fromFile(filePath, DEFAULT_BUFFER_SIZE);
    }

    public static BufferedInput fromResource(String srcPath, int bufSize) {
        try {
            InputStream stream = BufferedInput.class.getResourceAsStream(srcPath);
            if (stream == null) {
                throw new FileNotFoundException(srcPath);
            }
            return new BufferedInput(new FileByteReader(stream), bufSize);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedInput fromResource(String srcPath) {
        return fromResource(srcPath, DEFAULT_BUFFER_SIZE);
    }

    public static BufferedInput fromConsole(int bufSize) {
        return new BufferedInput(new ConsoleByteReader(), bufSize);
    }

    public static BufferedInput fromConsole() {
        return new BufferedInput(new ConsoleByteReader());
    }

    @Override
    public boolean isOpen() {
        return buffer != null;
    }

    @Override
    public boolean available() {
        if (!hasNext()) {
            return false;
        }
        if (lexemeStart <= forward) {
            return forward - lexemeStart < maxLexemeLen;
        }
        return forward + bufEndB - lexemeStart < maxLexemeLen;
    }

    @Override
    public int read() {
        checkAvailable();
        byte result = buffer[forward];
        forward++;
        if (forward == bufEndB) {
            fill(0);
            forward = 0;
        } else if (forward == bufEndA) {
            fill(bufEndA);
        }
        return result;
    }

    private void fill(int begin) {
        fillCount++;
        int size = reader.read(buffer, begin, bufEndA);
        if (size < bufEndA) {
            buffer[begin + size] = INPUT_END;
        }
    }

    @Override
    public int readUnsigned() {
        return read() & 0xFF;
    }

    @Override
    public boolean hasNext() {
        return buffer[forward] != INPUT_END;
    }

    @Override
    public String lexeme() {
        checkOpen();
        final int start = lexemeStart;
        lexemeStart = forward;
        if (start < forward) {
            return new String(buffer, start, forward - start, StandardCharsets.UTF_8);
        } else if (start > forward){
            int lenB = bufEndB - start;
            int lenA = forward;
            byte[] temp = new byte[lenB + lenA];
            System.arraycopy(buffer, start, temp, 0, lenB);
            System.arraycopy(buffer, 0, temp, lenB, lenA);
            return new String(temp, StandardCharsets.UTF_8);
        }
        return "";
    }

    @Override
    public void markLexemeStart() {
        lexemeStart = forward - 1;
    }

    @Override
    public void retract(int count) {
        checkOpen();
        int res = forward - count;
        if (res > 0) {
            forward = res;
            lexemeStart = Math.min(forward - 1, lexemeStart);
        } else {
            if (fillCount == 0) {
                throw new IllegalArgumentException("can not retract: buffer B not loaded");
            }
            if (res < 0) {
                res = bufEndB + res;
                if (res <= bufEndA) {
                    throw new IllegalArgumentException("can not retract: exceed retract limit");
                }
                lexemeStart = lexemeStart > bufEndA ? Math.min(res, lexemeStart) : res;
            } else if (lexemeStart < bufEndA) {
               lexemeStart = 0;
            }
            forward = res;
        }
    }

    @Override
    public void close() {
        buffer = null;
    }
}
