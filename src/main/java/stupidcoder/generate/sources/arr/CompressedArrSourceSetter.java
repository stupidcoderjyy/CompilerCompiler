package stupidcoder.generate.sources.arr;

import stupidcoder.util.ICompressedArraySetter;

public abstract class CompressedArrSourceSetter implements ICompressedArraySetter {
    private final Source2DArrSetter setter;

    public CompressedArrSourceSetter(Source2DArrSetter setter) {
        this.setter = setter;
    }

    @Override
    public void setData(int i, String val) {
        if (val != null) {
            setter.set(0, i, val);
        }
    }

    @Override
    public void setStart(int i, int pos) {
        setter.set(1, i, pos);
    }

    @Override
    public void setOffset(int i, int offset) {
        setter.set(2, i, offset);
    }
}
