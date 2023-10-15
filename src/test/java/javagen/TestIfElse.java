package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.Config;
import stupidcoder.core.CompilerGenerator;

public class TestIfElse {

    @Test
    public void test() {
        Config.set(
                Config.OUTPUT_DIR,
                "C:\\Users\\stupid_coder_jyy\\Desktop\\cs\\IdeaProjects\\GenTest\\src\\main\\java");
        Config.set(Config.SHOW_ACTION_CONFLICT, false);
        Config.set(Config.COMPRESSED_ARR, false);
        Config.set(Config.KEY_WORD, true);
        Config.set(Config.SYNTAX_DEBUG_INFO, true);
        Config.set(Config.LEXER_DEBUG_INFO, false);
        CompilerGenerator.gen("generate/compiler/testIfElse.txt", "");
    }
}
