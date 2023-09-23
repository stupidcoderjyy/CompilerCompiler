package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;

public class TestComplexTransform {

    @Test
    public void test1() {
        new Generator("test_out_complex_transform.txt",
                "generate/test_tr_complex.txt")
                .run();
    }
}
