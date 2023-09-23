package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.Source;
import stupidcoder.generate.SourceBuffered;
import stupidcoder.generate.SourceFieldString;

public class TestGenerator {
    private final String str = "1234567890";

    @Test
    public void test1() {
        Source src = new SourceFieldString("test_src", () -> str);
        Generator g = new Generator("test_out.txt", "generate/test_script_1.txt");
        g.registerSrc(src);
        g.run();
    }

    @Test
    public void test2() {
        Source src = new SourceFieldString("test_src", () -> str);
        Generator g = new Generator("test_out_2.txt", "generate/test_script_2.txt");
        g.registerSrc(src);
        g.run();
    }

    @Test
    public void test3() {
        SourceBuffered src = new SourceBuffered("test_src");
        for (int i = 0xFFF ; i < 0x1FFF ; i ++) {
            src.writeInt(i);
        }
        Generator g = new Generator("test_out_3.txt", "generate/test_script_3.txt");
        g.registerSrc(src);
        g.run();
    }
}
