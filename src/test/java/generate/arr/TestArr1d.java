package generate.arr;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.arr.Source1DArrSetter;
import stupidcoder.generate.sources.arr.SourceArrSetter;

public class TestArr1d {

    @Test
    public void test1() {
        Generator g = new Generator();
        Source1DArrSetter src = new Source1DArrSetter("src");
        src.set(1,1);
        src.set(2,2);
        src.set(3,3);
        src.set(4,4);
        g.registerSrc(src);
        g.loadScript("generate/out/arr/test.txt","test1d.txt");
    }

    @Test
    public void test2() {
        Generator g = new Generator();
        Source1DArrSetter src = new Source1DArrSetter("src", SourceArrSetter.FOLD_OPTIMIZE);
        for (int i = 0 ; i < 3 ; i ++) {
            src.set(i, 10);
            src.set(i, 12);
            src.set(i, 13);
            src.set(i, 14);
        }
        src.set(12, 14);
        src.set(15, 14);
        g.registerSrc(src);
        g.loadScript("generate/out/arr/test.txt","test1d.txt");
    }

    @Test
    public void test3() {
        Generator g = new Generator();
        Source1DArrSetter src = new Source1DArrSetter("src", SourceArrSetter.EXTRACT_COMMON_DATA);
        src.set(1, "1");
        src.set(2, "1");
        src.set(4, "1");
        src.set(8, "1");
        src.set(9, "2");
        src.set(10, "2");
        src.set(13, "3");
        g.registerSrc(src);
        g.loadScript("generate/out/arr/test.txt","test1d.txt");
    }
}
