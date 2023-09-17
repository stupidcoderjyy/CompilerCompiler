package fieldparser;

import org.junit.jupiter.api.Test;
import stupidcoder.fieldparser.Env;
import stupidcoder.util.input.StringInput;

public class TestParser {
    @Test
    public void test() {
        Env env = Env.fromInput(new StringInput("test: \"dwahdiwa\";  test2: [\"125\", \"555\"];"));
        System.out.println(env);
    }
}
