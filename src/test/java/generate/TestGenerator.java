package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.Source;
import stupidcoder.generate.source.CacheSource;
import stupidcoder.generate.source.field.StringFieldSource;

public class TestGenerator {
    private String str = "1234567890";

    @Test
    public void test1() {
        Source src = new StringFieldSource("test_src", () -> str);
        Generator g = new Generator("test_out.txt", "generate/test_script_1.txt");
        g.registerSrc(src);
        g.run();
    }

    @Test
    public void test2() {
        Source src = new StringFieldSource("test_src", () -> str);
        Generator g = new Generator("test_out_2.txt", "generate/test_script_2.txt");
        g.registerSrc(src);
        g.run();
    }

    @Test
    public void test3() {
        CacheSource src = new CacheSource("test_src");
        for (int i = 0 ; i < 500 ; i ++) {
            src.writeInt(i);
        }
        Generator g = new Generator("test_out_3.txt", "generate/test_script_3.txt");
        g.registerSrc(src);
        g.run();
    }
}
