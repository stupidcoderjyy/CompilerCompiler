package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.Config;
import stupidcoder.PathUtil;
import stupidcoder.core.CompilerGenerator;

public class TestInterpreterGen {

    @Test
    public void test() {
        long l1 = System.currentTimeMillis();
        Config.set(
                Config.OUTPUT_DIR,
                PathUtil.desktopPath("cs\\IdeaProjects\\GenCalculator\\src\\main\\java"));
        Config.set(Config.SHOW_ACTION_CONFLICT, false);
        Config.set(Config.COMPRESSED_ARR, false);
        Config.set(Config.KEY_WORD, true);
        Config.set(Config.SYNTAX_DEBUG_INFO, false);
        CompilerGenerator.gen("generate/compiler/interpreter.txt", "");
        System.out.println(System.currentTimeMillis() - l1);
    }
}
