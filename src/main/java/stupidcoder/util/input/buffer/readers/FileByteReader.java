package stupidcoder.util.input.buffer.readers;

import stupidcoder.util.input.buffer.IByteReader;

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

    /**
     * 从流中读取若干个字节，并装入一个字节数组中
     *
     * @param arr    待填充的字节数组
     * @param offset 字节数组中的存放起点
     * @param len    读取字节数
     * @return 实际读取的字节个数
     */
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
