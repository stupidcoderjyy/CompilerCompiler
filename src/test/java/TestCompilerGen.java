import org.junit.jupiter.api.Test;
import stupidcoder.core.CompilerGenerator;
import stupidcoder.util.Config;

public class TestCompilerGen {

    @Test
    public void testGenCalculator() {
        CompilerGenerator.enableSyntaxDebugInfo();
        CompilerGenerator.enableSyntaxConflictInfo();
        CompilerGenerator.gen("generate/compiler/calculator.txt");
    }

    @Test
    public void testGenImportParser() {
        Config.set(Config.GLOBAL_OUTPUT_DIR,
                "C:\\Users\\stupid_coder_jyy\\Desktop\\cs\\IdeaProjects\\GenTest\\src\\main\\java");
        CompilerGenerator.gen(
                "generate/compiler/importParser.txt",
                "stupidcoder.importParser");
    }

    @Test
    public void testGenInterpreter() {
        CompilerGenerator.enableKeyWordToken();
        CompilerGenerator.gen("generate/compiler/interpreter.txt");
    }

    @Test
    public void testGenScriptLoader() {
        CompilerGenerator.enableCompressedArr();
        CompilerGenerator.enableSyntaxDebugInfo();
        CompilerGenerator.enableLexerDebugInfo();
        CompilerGenerator.enableSyntaxConflictInfo();
        CompilerGenerator.gen("generate/compiler/scriptloader.txt");
    }
}
