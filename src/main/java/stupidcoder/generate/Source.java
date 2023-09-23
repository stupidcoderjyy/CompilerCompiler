package stupidcoder.generate;

import stupidcoder.util.input.IByteReader;

/**
 * 源是一个能输出字节流的结构，在文档输出器中，源输出字节，进入转换器{@link Transform}转化成字符流
 * 写入文件。源有3种状态：初始状态、锁定状态和关闭状态。初始状态可以写入数据，但不可读；锁定状态只可读
 * 不可写；关闭状态既不可读也不可写。<p>
 * 一个源一次只能向一个转换器连续输出，输出后不可再次被调用，如果必须要调用则可调用{@link Source#reset()}
 * 重置源的状态
 */
public abstract class Source implements IByteReader {
    protected final String id;
    protected boolean used = false;

    public Source(String id) {
        this.id = id;
    }

    /**
     * 锁定源，此时源只可读，不可写
     */
    protected abstract void lock();

    /**
     * 重置源的状态（在任何状态下都可以调用）。重置后默认为锁定状态
     * @throws UnsupportedOperationException 对于不可重置的源，如果尝试重复调用，则会抛出
     */
    protected void reset() {
        throw new UnsupportedOperationException();
    }

    public static Source EMPTY = new Source("") {
        @Override
        protected void lock() {
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
