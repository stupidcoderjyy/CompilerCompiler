package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;

public class TestUnescape {

    @Test
    public void test() {
        Generator g = new Generator();
        g.loadScript("generate/out/unescape.txt", "test_unescape.txt");
    }
}
