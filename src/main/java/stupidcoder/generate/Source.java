package stupidcoder.generate;

import stupidcoder.util.input.IByteReader;

public abstract class Source implements IByteReader {
    protected final String name;
    protected boolean used = false;

    public Source(String name) {
        this.name = name;
    }

    public abstract void lock();

    protected void reset() {
        throw new UnsupportedOperationException();
    }

    public void destroy() {
        close();
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
