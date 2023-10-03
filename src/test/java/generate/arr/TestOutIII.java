package generate.arr;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.arr.HighFreqPoint;
import stupidcoder.generate.sources.arr.SourceArrSetterIII;

public class TestOutIII {

    @Test
    public void test() {
        Generator g = new Generator();
        SourceArrSetterIII setter = new SourceArrSetterIII("src", HighFreqPoint.ARG_2);
        g.registerSrc(setter);
        for (int i = 0; i < 3; i++) {
            setter.set(1, i, 6);
        }
        setter.set(2, 5, 2);
        g.loadScript("generate/out/arr/III.txt", "arr_iii.txt");
    }
}
