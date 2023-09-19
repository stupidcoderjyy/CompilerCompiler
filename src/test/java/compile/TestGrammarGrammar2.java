package compile;

import org.junit.jupiter.api.Test;
import stupidcoder.compile.grammar.IInitGrammar;
import stupidcoder.compile.grammar.LRGroupBuilder;

public class TestGrammarGrammar2 {

    @Test
    public void test() {
        LRGroupBuilder.build(p -> {}, GRAMMAR_INIT, DefaultDataInterface.ACCEPT);
    }

    private static final IInitGrammar GRAMMAR_INIT = loader -> {
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
                .addTerminal("end", 132)
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
    };
}
