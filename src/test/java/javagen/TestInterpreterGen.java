package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.core.CompilerGenerator;

public class TestInterpreterGen {

    @Test
    public void test() {
        CompilerGenerator.gen("generate/compiler/interpreter.txt");
    }
}
