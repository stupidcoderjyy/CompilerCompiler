package compile;

import org.junit.jupiter.api.Test;
import stupidcoder.compile.grammar.IGAGrammarInit;
import stupidcoder.compile.grammar.LRGroupBuilder;

import java.io.FileOutputStream;

public class TestBookExample {

    @Test
    public void test() {
        LRGroupBuilder.build(p -> {}, GRAMMAR_INIT, DefaultDataInterface.ACCEPT);
        try {

            FileOutputStream s = new FileOutputStream("test.txt");
            s.write('a');
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final IGAGrammarInit GRAMMAR_INIT = loader -> {
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
