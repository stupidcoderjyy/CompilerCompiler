package compile;

import org.junit.jupiter.api.Test;
import stupidcoder.compile.grammar.IInitGrammar;
import stupidcoder.compile.grammar.LRGroupBuilder;

public class TestBookExample {

    @Test
    public void test() {
        LRGroupBuilder.build(p -> {}, GRAMMAR_INIT, DefaultDataInterface.ACCEPT);
    }

    private static final IInitGrammar GRAMMAR_INIT = loader -> {
        loader.begin("S")
                .addNonTerminal("L")
                .addTerminal('=')
                .addNonTerminal("R")
                .finish();
        loader.begin("S")
                .addNonTerminal("R")
                .finish();
        loader.begin("L")
                .addTerminal('*')
                .addNonTerminal("R")
                .finish();
        loader.begin("L")
                .addTerminal("id", 128)
                .finish();
        loader.begin("R")
                .addNonTerminal("L")
                .finish();
    };
}
