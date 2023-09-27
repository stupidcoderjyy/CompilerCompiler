package stupidcoder.generate;

import stupidcoder.util.input.IByteReader;

public abstract class Source implements IByteReader {
    protected final String id;
    protected boolean used = false;

    public Source(String id) {
        this.id = id;
    }

    public abstract void lock();

    protected void reset() {
        throw new UnsupportedOperationException();
    }

    public static Source EMPTY = new Source("") {
        @Override
        public void lock() {
        }

        @Override
        public int read(byte[] bytes, int i, int i1) {
            return 0;
        }

        @Override
        public void close() {
        }
    };
}
