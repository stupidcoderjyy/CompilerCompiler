package stupidcoder.generate.sources.arr;

import stupidcoder.generate.sources.SourceCached;

@FunctionalInterface
public interface IArrDataSourceSetter {
    void setSource(SourceCached src, String key);
}
