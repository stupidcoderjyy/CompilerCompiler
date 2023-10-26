package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.Config;
import stupidcoder.PathUtil;
import stupidcoder.core.CompilerGenerator;

public class TestCompilerGen {

    @Test
    public void test() {
        Config.set(
                Config.GLOBAL_OUTPUT_DIR,
                PathUtil.desktopPath("cs\\IdeaProjects\\GenCalculator\\src\\main\\java"));
        Config.set(Config.SYNTAX_SHOW_ACTION_CONFLICT, false);
        Config.set(Config.GEN_USE_COMPRESSED_ARR, true);
        CompilerGenerator.gen("generate/compiler/test.txt", "");
    }
}
