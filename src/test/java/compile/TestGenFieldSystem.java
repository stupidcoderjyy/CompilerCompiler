package compile;

import org.junit.jupiter.api.Test;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.compile.grammar.IGAGrammarInit;
import stupidcoder.compile.grammar.LRGroupBuilder;

public class TestGenFieldSystem {
    @Test
    public void test() {
        LRGroupBuilder.build(p -> {}, GRAMMAR_INIT, DefaultDataInterface.ACCEPT);
    }

    private static final IGAGrammarInit GRAMMAR_INIT = loader -> {
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
                .addNonTerminal("strings")
                .addTerminal(']')
                .finish();
        loader.begin("strings")
                .addTerminal("string", 129)
                .finish();
        loader.begin("strings")
                .addNonTerminal("strings")
                .addTerminal(',')
                .addTerminal("string", 129)
                .finish();
        loader.begin("strings")
                .addSymbol(DefaultSymbols.EPSILON)
                .finish();
    };
}
