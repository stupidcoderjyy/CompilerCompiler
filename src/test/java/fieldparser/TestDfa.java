package fieldparser;

import org.junit.jupiter.api.Test;
import stupidcoder.fieldparser.internal.DFA;
import stupidcoder.util.input.StringInput;

public class TestDfa {

    @Test
    public void testDfa() {
        StringInput input = new StringInput("a245 \"1234\" \"\" 213123213");
        DFA dfa = new DFA(input);
        while (input.available()) {
            System.out.println(dfa.run());
        }
    }
}
