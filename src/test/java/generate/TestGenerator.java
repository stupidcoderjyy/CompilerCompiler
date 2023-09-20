package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.Source;
import stupidcoder.generate.source.field.StringFieldSource;

public class TestGenerator {
    private String str = "1234567890";

    @Test
    public void test1() {
        Source src = new StringFieldSource("test_src", () -> str);
        Generator g = new Generator("test_out.txt", "generate/test_script.txt");
        g.registerSrc(src);
        g.run();
    }
}
