package generate.arr;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.arr.Source2DArrSetter;
import stupidcoder.generate.sources.arr.SourceArrSetter;

public class TestArr2d {

    @Test
    public void test1() {
        Generator g = new Generator();
        Source2DArrSetter src = new Source2DArrSetter("src");
        src.set(1,1,1);
        src.set(2,2,2);
        src.set(3,3,3);
        src.set(4,4,4);
        g.registerSrc(src);
        g.loadScript("generate/out/arr/test.txt","test2d.txt");
    }

    @Test
    public void test2() {
        Generator g = new Generator();
        Source2DArrSetter src = new Source2DArrSetter("src",
                SourceArrSetter.FOLD_OPTIMIZE | SourceArrSetter.EXTRACT_COMMON_DATA);
        for (int i = 0 ; i < 3 ; i ++) {
            src.set(10, i, 10);
            src.set(14, i, 12);
            src.set(13, i, 10);
            src.set(12, i, 12);
        }
        g.registerSrc(src);
        g.loadScript("generate/out/arr/test.txt","test2d.txt");
    }
}
