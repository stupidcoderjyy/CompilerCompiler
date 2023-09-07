package stupidcoder.util.input.buffer;

import java.io.Closeable;

/**
 * 字节的读取器
 * @author stupid_coder_jyy
 */
public interface IByteReader extends Closeable {
    /**
     * 从流中读取若干个字节，并装入一个字节数组中
     * @param arr 待填充的字节数组
     * @param offset 字节数组中的存放起点
     * @param len 读取字节数
     * @return 实际读取的字节个数
     */
    int read(byte[] arr, int offset, int len);

    @Override
    void close();
}
