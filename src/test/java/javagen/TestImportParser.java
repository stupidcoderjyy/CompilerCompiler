package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.Config;
import stupidcoder.PathUtil;
import stupidcoder.core.CompilerGenerator;

public class TestImportParser {

    @Test
    public void test() {
        Config.set(
                Config.OUTPUT_DIR,
                PathUtil.desktopPath("cs\\IdeaProjects\\GenCalculator\\src\\main\\java"));
        Config.set(Config.SHOW_ACTION_CONFLICT, true);
        Config.set(Config.COMPRESSED_ARR, false);
        CompilerGenerator.gen("generate/compiler/importParser.txt", "");
    }
}
