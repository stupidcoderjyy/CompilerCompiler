package com.stupidcoder.cc.util.input.buffer.readers;


import com.stupidcoder.cc.util.input.buffer.IByteReader;

/**
 * @author stupid_coder_jyy
 */
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
