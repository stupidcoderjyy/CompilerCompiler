package com.stupidcoder.cc.util.input.buffer;


public class Buffer {
    public static final int DEFAULT_BUFFER_SIZE = 4096;

    private byte[] buffer;

    /**
     * 缓冲区的实际大小
     */
    private final int bufferSize;

    /**
     * 指向有效缓冲区的末尾，即最后一个有效字符的下一个位置
     */
    private int bufferEnd;

    /**
     * 缓冲区是否启动
     */
    private boolean isOpened;


    private final IByteReader reader;

    public Buffer(IByteReader reader) {
        this(DEFAULT_BUFFER_SIZE, reader);
    }

    public Buffer(int size, IByteReader reader) {
        if (size > 1) {
            this.reader = reader;
            this.bufferSize = size;
            return;
        }
        throw new IllegalArgumentException("无效缓冲区大小:" + size);
    }

    public void open() {
        isOpened = reader.open();
        if (buffer == null || buffer.length != bufferSize) {
            buffer = new byte[bufferSize];
        }
        bufferEnd = 0;
    }

    public void close() {
        isOpened = false;
        buffer = null;
        bufferEnd = 0;
    }

    /**
     * 填充缓冲区
     */
    public void load() {
        if (!isOpened) {
            throw new IllegalStateException("缓冲区未开启");
        }
        bufferEnd = reader.read(buffer, 0, bufferSize);
    }

    /**
     * 是否能够从缓冲区中读取某个位置的数据
     * @param pos 目标位置
     * @return 可以读取返回true
     */
    public boolean available(int pos) {
        return pos >= 0 && pos < bufferEnd;
    }

    public byte get(int i) {
        if (available(i)) {
            return buffer[i];
        }
        throw new IndexOutOfBoundsException("超过缓冲区边界:" + i);
    }

    public final int size() {
        return bufferSize;
    }

    public final byte[] data() {
        return buffer;
    }
}
