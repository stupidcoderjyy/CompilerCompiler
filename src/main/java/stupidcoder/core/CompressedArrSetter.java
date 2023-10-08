package stupidcoder.core;

import stupidcoder.generate.sources.arr.Source2DArrSetter;
import stupidcoder.util.ICompressedArraySetter;

public abstract class CompressedArrSetter implements ICompressedArraySetter {
    private final Source2DArrSetter setter;

    public CompressedArrSetter(Source2DArrSetter setter) {
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
