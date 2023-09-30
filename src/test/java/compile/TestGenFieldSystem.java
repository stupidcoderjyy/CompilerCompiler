package compile;

import org.junit.jupiter.api.Test;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.compile.syntax.LRGroupBuilder;
import stupidcoder.compile.syntax.SyntaxLoader;

public class TestGenFieldSystem {
    @Test
    public void test() {
        SyntaxLoader loader = new SyntaxLoader();

        loader.begin("stmts")
                .addNonTerminal("stmt")
                .finish();
        loader.begin("stmts")
                .addNonTerminal("stmts")
                .addNonTerminal("stmt")
                .finish();
        loader.begin("stmt")
                .addTerminal("id", 128)
                .addTerminal(':')
                .addNonTerminal("val")
                .addTerminal(';')
                .finish();
        loader.begin("val")
                .addTerminal("string", 129)
                .finish();
        loader.begin("val")
                .addNonTerminal("list")
                .finish();
        loader.begin("list")
                .addTerminal('[')
                .addNonTerminal("content")
                .addTerminal(']')
                .finish();
        loader.begin("content")
                .add(DefaultSymbols.EPSILON)
                .finish();
        loader.begin("content")
                .addNonTerminal("strings")
                .finish();
        loader.begin("strings")
                .addTerminal("string", 129)
                .finish();
        loader.begin("strings")
                .addNonTerminal("strings")
                .addTerminal(',')
                .addTerminal("string", 129)
                .finish();
        LRGroupBuilder.build(loader, DefaultDataInterface.ACCEPT);
    }
}
