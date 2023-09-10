import org.junit.jupiter.api.Test;
import stupidcoder.grammar.IGADataAccept;
import stupidcoder.grammar.IGAGrammarInit;
import stupidcoder.grammar.LRGroupBuilder;

public class TestGrammarGrammar2 {

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
