import org.junit.jupiter.api.Test;
import stupidcoder.core.CompilerGenerator;
import stupidcoder.util.Config;

public class TestCompilerGen {

    @Test
    public void testGenCalculator() {
        Config.set(Config.GLOBAL_OUTPUT_DIR, "C:\\Users\\stupid_coder_jyy\\Desktop\\cs\\IdeaProjects\\GenTest\\src");
        CompilerGenerator.gen("generate/compiler/calculator.txt", "stupidcoder.calculator");
    }

    @Test
    public void testGenImportParser() {
        CompilerGenerator.enableKeyWordToken();
        CompilerGenerator.gen(
                "generate/compiler/importParser.txt",
                "stupidcoder.importParser");
    }

    @Test
    public void testGenInterpreter() {
//        CompilerGenerator.enableCompressedArr();
        CompilerGenerator.enableKeyWordToken();
        CompilerGenerator.gen("generate/compiler/interpreter.txt");
    }

    @Test
    public void testGenScriptLoader() {
        CompilerGenerator.enableLexerDebugInfo();
        CompilerGenerator.gen("generate/compiler/scriptloader.txt", "stupidcoder.core.scriptloader");
    }
}
