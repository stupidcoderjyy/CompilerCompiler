package stupidcoder.util.compile.symbol;

public class DefaultSymbols {
    public static final Symbol EPSILON = new Symbol("ε", true, -1);
    public static final Symbol ROOT = new Symbol("root", false, 0);
    public static final Symbol EOF = new Symbol("$EOF$", true, 0);
}