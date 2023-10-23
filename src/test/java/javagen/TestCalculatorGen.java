package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.Config;
import stupidcoder.core.CompilerGenerator;

public class TestCalculatorGen {

    @Test
    public void test() {
        Config.set(Config.OUTPUT_DIR, "C:\\Users\\stupid_coder_jyy\\Desktop\\cs\\IdeaProjects\\GenCalculator\\src");
        CompilerGenerator.gen(
                "generate/compiler/calculator.txt",
                "");
    }
}
