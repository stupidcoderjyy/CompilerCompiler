package stupidcoder.util;

public interface ICompressedArraySetter {
    void setData(int i, String val);
    void setStart(int i, int pos);
    void setOffset(int i, int offset);
    void setSize(int dataSize, int startSize, int offsetsSize);
}
