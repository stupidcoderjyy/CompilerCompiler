package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.Config;
import stupidcoder.core.CompilerGenerator;

public class TestJavaGen {

    @Test
    public void testImport() {
        Config.set(Config.SYNTAX_DEBUG_INFO, true);
        CompilerGenerator.gen("generate/compiler/test.txt", "");
    }
}
