package compile;

import org.junit.jupiter.api.Test;
import stupidcoder.compile.syntax.LRGroupBuilder;
import stupidcoder.compile.syntax.SyntaxLoader;

public class TestGrammarGrammar2 {

    @Test
    public void test() {
        SyntaxLoader loader = new SyntaxLoader();
        loader.begin("gs")
                .addNonTerminal("g").finish();
        loader.begin("gs")
                .addNonTerminal("gs")
                .addNonTerminal("g")
                .finish();
        loader.begin("g")
                .addTerminal("@nt", 129)
                .addTerminal("@p", 130)
                .addNonTerminal("prod")
                .addTerminal(';')
                .finish();
        loader.begin("prod")
                .addNonTerminal("seq")
                .finish();
        loader.begin("prod")
                .addNonTerminal("prod")
                .addTerminal('|')
                .addNonTerminal("seq")
                .finish();
        loader.begin("seq").addNonTerminal("symbol").finish();
        loader.begin("seq").addNonTerminal("seq").addNonTerminal("symbol").finish();
        loader.begin("symbol").addTerminal("@nt", 129).finish();
        loader.begin("symbol").addTerminal("@t", 131).finish();
        LRGroupBuilder.build(loader, DefaultDataInterface.ACCEPT);
    }
}
