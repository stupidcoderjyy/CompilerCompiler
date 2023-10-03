package generate.arr;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.arr.SourceArrSetterIS;

public class TestOutIS {

    @Test
    public void test() {
        Generator g = new Generator();
        SourceArrSetterIS setter = new SourceArrSetterIS("src");
        g.registerSrc(setter);
        setter.set(1, "a");
        setter.set(2, "a");
        setter.set(3, "b");
        setter.set(4, "b");
        g.loadScript("generate/out/arr/IS.txt", "arr_is.txt");
    }
}
