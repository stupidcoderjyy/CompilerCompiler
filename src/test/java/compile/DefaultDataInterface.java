package compile;

import stupidcoder.compile.syntax.ISyntaxAnalyzerSetter;
import stupidcoder.compile.syntax.SyntaxLoader;

public class DefaultDataInterface {
    public static final ISyntaxAnalyzerSetter ACCEPT = new ISyntaxAnalyzerSetter() {
        @Override
        public void setActionShift(int from, int to, int inputTerminal) {
            System.out.printf("actions[%d][%d] = MOVE | %d;\n", from, inputTerminal, to);
        }

        @Override
        public void setActionReduce(int state, int forward, int productionId) {
            System.out.printf("actions[%d][%d] = REDUCE | %d;\n", state, forward, productionId);
        }

        @Override
        public void setActionAccept(int state, int forward) {
            System.out.printf("actions[%d][%d] = ACCEPT;\n", state, forward);
        }

        @Override
        public void setGoto(int from, int to, int inputNonTerminal) {
            System.out.printf("goTo[%d][%d] = %d;\n", from, inputNonTerminal, to);
        }

        @Override
        public void setStatesCount(int count) {
            System.out.println("states: " + count);
        }

        @Override
        public void setOthers(SyntaxLoader access) {

        }
    };
}
