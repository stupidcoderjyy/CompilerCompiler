package stupidcoder.util.input;

public class BitClass {
    public static final BitClass BLANK = BitClass.of(' ', '\t', '\r', '\n');
    public static final BitClass NUMBER = BitClass.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    private final boolean[] data = new boolean[128];

    public static BitClass of(int ... chs) {
        BitClass clazz = new BitClass();
        for (int ch : chs) {
            clazz.add(ch);
        }
        return clazz;
    }

    BitClass() {
    }

    void add(int b) {
        b &= 0xFF;
        if (b > 127) {
            return;
        }
        data[b] = true;
    }

    public boolean accept(int b) {
        b &= 0xFF;
        if (b > 127) {
            return false;
        }
        return data[b];
    }
}
