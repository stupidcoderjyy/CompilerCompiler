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
                PathUtil.desktopPath("cs\\IdeaProjects\\GenTest\\src\\main\\java"));
        Config.set(Config.LEXER_DEBUG_INFO, true);
        Config.set(Config.KEY_WORD, true);
        CompilerGenerator.gen("generate/compiler/importParser.txt", "");
    }
}
