package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.Config;
import stupidcoder.PathUtil;
import stupidcoder.core.CompilerGenerator;

public class TestCompilerGen {

    @Test
    public void test() {
        Config.set(
                Config.OUTPUT_DIR,
                PathUtil.desktopPath("cs\\IdeaProjects\\GenCalculator\\src\\main\\java"));
        CompilerGenerator.gen("generate/compiler/test.txt", "");
    }
}
