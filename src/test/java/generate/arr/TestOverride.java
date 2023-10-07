package generate.arr;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.SourceCached;
import stupidcoder.generate.sources.arr.IArrDataSourceSetter;
import stupidcoder.generate.sources.arr.Source2DArrSetter;
import stupidcoder.generate.sources.arr.SourceArrSetter;

import java.util.Random;

public class TestOverride implements IArrDataSourceSetter {

    @Test
    public void test1() {
        Generator g = new Generator();
        Source2DArrSetter src = new Source2DArrSetter("src",
                SourceArrSetter.FOLD_OPTIMIZE | SourceArrSetter.EXTRACT_COMMON_DATA);
        for (int i = 0 ; i < 3 ; i ++) {
            src.set(10, i, 10, this);
            src.set(14, i, 12, this);
            src.set(13, i, 10, this);
            src.set(12, i, 12, this);
        }
        g.registerSrc(src);
        g.loadScript("generate/out/arr/override.txt","testOverride.txt");
    }

    @Test
    public void test2() {
        Generator g = new Generator();
        Source2DArrSetter src = new Source2DArrSetter("src",
                SourceArrSetter.FOLD_OPTIMIZE | SourceArrSetter.EXTRACT_COMMON_DATA);
        src.overrideGlobalSetter(this);
        for (int i = 0 ; i < 3 ; i ++) {
            src.set(10, i, 10);
            src.set(14, i, 12);
            src.set(13, i, 10);
            src.set(12, i, 12);
        }
        g.registerSrc(src);
        g.loadScript("generate/out/arr/override.txt","testOverride.txt");
    }

    @Override
    public void setSource(SourceCached src, String key) {
        src.writeInt(new Random().nextInt(3));
        src.writeString(key);
    }
}
