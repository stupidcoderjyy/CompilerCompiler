package stupidcoder.generate.sources.arr;

public enum HighFreqPoint {
    ARG_1((src, key1, key2, hf) -> {
        src.writeInt(hf);
        src.writeInt(key1);
        src.writeInt(key2);
    }),
    ARG_2((src, key1, key2, hf) -> {
        src.writeInt(key1);
        src.writeInt(hf);
        src.writeInt(key2);
    }),
    DATA((src, key1, key2, hf) -> {
        src.writeInt(key1);
        src.writeInt(key2);
        src.writeInt(hf);
    });
    final SingleStmtWriter singleSetter;

    HighFreqPoint(SingleStmtWriter singleSetter) {
        this.singleSetter = singleSetter;
    }
}
