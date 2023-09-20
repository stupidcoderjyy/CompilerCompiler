package stupidcoder.generate.source.field;

import stupidcoder.generate.Source;

import java.util.function.Supplier;

public abstract class FieldSource<T> extends Source {
    protected int size;
    protected byte[] data;
    private Supplier<T> supplier;
    private int pos;

    public FieldSource(String id, Supplier<T> supplier) {
        super(id);
        this.supplier = supplier;
    }

    protected abstract int getSize(T val);

    protected byte[] getBytes() {
        return new byte[size];
    }

    protected abstract void writeBytes(T val);

    @Override
    public void lock() {
        T v = supplier.get();
        size = getSize(v);
        data = getBytes();
        if (data == null) {
            throw new NullPointerException("null data");
        }
        if (size < 0) {
            throw new IllegalStateException("minus size");
        }
        writeBytes(v);
    }

    @Override
    public int read(byte[] arr, int offset, int len) {
        int actualLen = Math.min(len, size - pos);
        if (actualLen == 0) {
            return 0;
        }
        System.arraycopy(data, pos, arr, offset, actualLen);
        pos += actualLen;
        return size;
    }

    @Override
    public void close() {
        data = null;
        supplier = null;
    }

    protected final void writeInt(int v, int start) {
        data[start] = (byte) (v >> 24);
        data[start + 1] = (byte) (v >> 16);
        data[start + 2] = (byte) (v >> 8);
        data[start + 3] = (byte) v;
    }
}
