import org.junit.jupiter.api.Test;
import stupidcoder.core.CompilerGenerator;
import stupidcoder.util.Config;

public class TestCompilerGen {

    @Test
    public void testGenCalculator() {
        Config.set(Config.GLOBAL_OUTPUT_DIR, "C:\\Users\\stupid_coder_jyy\\Desktop\\cs\\IdeaProjects\\GenTest\\src");
        CompilerGenerator.genJava("generate/compiler/calculator.txt", "stupidcoder.calculator");
    }

    @Test
    public void testGenImportParser() {
        CompilerGenerator.enableKeyWordToken();
        CompilerGenerator.genJava(
                "generate/compiler/importParser.txt",
                "stupidcoder.importParser");
    }

    @Test
    public void testGenInterpreter() {
//        CompilerGenerator.enableCompressedArr();
        CompilerGenerator.enableKeyWordToken();
        CompilerGenerator.genJava("generate/compiler/interpreter.txt");
    }

    @Test
    public void testGenScriptLoader() {
        CompilerGenerator.enableLexerDebugInfo();
        CompilerGenerator.genJava("generate/compiler/scriptloader.txt", "stupidcoder.core.scriptloader");
    }

    @Test
    public void testGenNbtParser() {
        CompilerGenerator.enableKeyWordToken();
        CompilerGenerator.genCpp("generate/compiler/nbt.txt", "LifeRhythm");
    }
}
