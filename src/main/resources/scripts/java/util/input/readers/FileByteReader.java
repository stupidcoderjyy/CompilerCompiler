package stupidcoder.util.input.readers;

import stupidcoder.util.input.IByteReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileByteReader implements IByteReader {
    private final InputStream stream;

    public FileByteReader(FileInputStream stream) {
        this.stream = stream;
    }

    public FileByteReader(String filePath) throws FileNotFoundException {
        this.stream = new FileInputStream(filePath);
    }

    public FileByteReader(InputStream stream) {
        this.stream = stream;
        if (stream == null) {
            throw new NullPointerException("null stream");
        }
    }

    @Override
    public int read(byte[] arr, int offset, int len) {
        try {
            return Math.max(0, stream.read(arr, offset, len));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void close() {
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
