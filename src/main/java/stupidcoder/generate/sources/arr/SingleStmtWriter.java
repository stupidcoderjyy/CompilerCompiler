package stupidcoder.generate.sources.arr;

import stupidcoder.generate.sources.SourceCached;

@FunctionalInterface
interface SingleStmtWriter {
    void write(SourceCached src, int key1, int key2, int hf);
}
