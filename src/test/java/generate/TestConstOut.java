package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;

public class TestConstOut {

    @Test
    public void testNormal() {
        Generator g = new Generator();
        g.loadScript("/generate/out/const/basic.txt", "const_basic.txt");
    }

    @Test
    public void testError() {
        Generator g = new Generator();
        for (int i = 1 ; i <= 8 ; i ++) {
            g.loadScript(String.format("/generate/out/const/err%d.txt", i), "err.txt");
        }
    }
}
