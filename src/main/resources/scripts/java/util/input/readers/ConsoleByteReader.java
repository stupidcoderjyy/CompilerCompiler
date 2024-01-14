package stupidcoder.util.input.readers;

import stupidcoder.util.input.IByteReader;

public class ConsoleByteReader implements IByteReader {

    @Override
    public void close() {
        //常量流
    }

    @Override
    public int read(byte[] arr, int offset, int len) {
        try {
            return Math.max(0, System.in.read(arr, offset, len));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
