package lex;

import org.junit.jupiter.api.Test;
import stupidcoder.lex.NFARegexParser;

public class TestRegexParserError {

    @Test
    public void test1() {
        NFARegexParser parser = new NFARegexParser();
        try {
            parser.register("[a", "");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void test2() {
        NFARegexParser parser = new NFARegexParser();
        try {
            parser.register("a|", "");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void test3() {
        NFARegexParser parser = new NFARegexParser();
        try {
            parser.register("ab)", "");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void test4() {
        NFARegexParser parser = new NFARegexParser();
        try {
            parser.register("(ab", "");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }
}
