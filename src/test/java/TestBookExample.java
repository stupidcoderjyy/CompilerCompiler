import org.junit.jupiter.api.Test;
import stupidcoder.grammar.IGADataAccept;
import stupidcoder.grammar.IGAGrammarInit;
import stupidcoder.grammar.LRGroupBuilder;

public class TestBookExample {

    @Test
    public void test() {
        LRGroupBuilder.build(p -> {}, GRAMMAR_INIT, ACCEPT);
    }

    private static final IGADataAccept ACCEPT = new IGADataAccept() {
        @Override
        public void setActionShift(int from, int to, int inputTerminal) {
            System.out.printf("ACTION[%d][%d] = MOVE | %d\n", from, inputTerminal, to);
        }

        @Override
        public void setActionReduce(int state, int forward, int productionId) {
            System.out.printf("ACTION[%d][%d] = REDUCE | %d\n", state, forward, productionId);
        }

        @Override
        public void setActionAccept(int state) {
            System.out.println("accepted:" + state);
        }

        @Override
        public void setGoto(int from, int to, int inputNonTerminal) {
            System.out.printf("GOTO[%d, %d] = %d\n", from, inputNonTerminal, to);
        }

        @Override
        public void setTerminalSymbolIdRemap(int origin, int after) {
            System.out.println(origin + "->" + after);
        }
    };

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
