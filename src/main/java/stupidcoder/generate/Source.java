package stupidcoder.generate;

import stupidcoder.util.input.IByteReader;

public abstract class Source implements IByteReader {
    protected final String id;

    public Source(String id) {
        this.id = id;
    }

    protected abstract void lock();
}
