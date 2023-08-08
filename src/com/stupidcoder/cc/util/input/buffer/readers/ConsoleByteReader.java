package com.stupidcoder.cc.util.input.buffer.readers;


import com.stupidcoder.cc.util.input.buffer.IByteReader;

/**
 * @author stupid_coder_jyy
 */
public class ConsoleByteReader implements IByteReader {

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public void close() {
        //常量流
    }

    @Override
    public int read(byte[] arr, int offset, int len) {
        try {
            return System.in.read(arr, offset, len);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
