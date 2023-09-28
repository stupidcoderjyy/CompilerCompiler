package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.compile.lex.DFABuilder;
import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.core.LexerGenerator;
import stupidcoder.generate.generators.java.JProjectBuilder;

public class TestDfaGen {

    @Test
    public void test1() {
        JProjectBuilder builder = new JProjectBuilder("srcLexerGen", "lexerGen");
        NFARegexParser parser = new NFARegexParser();
        parser.register("@d+(L|l)?|0(x|X)@h+", "Integer");
        parser.register("@a@w*", "Word");
        DFABuilder.build(new LexerGenerator(builder), parser);
        builder.gen();
    }
}
