package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.compile.lex.DFABuilder;
import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.compile.syntax.LRGroupBuilder;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.LexerSourceGen;
import stupidcoder.core.SyntaxAnalyzerSourceGen;
import stupidcoder.generate.generators.java.JProjectBuilder;

public class TestSyntaxParserGen {
    private SyntaxLoader loader;
    private NFARegexParser parser;

    @Test
    public void test() {
//        Config.set(Config.OUTPUT_DIR, PathUtil.desktopPath("cs\\IdeaProjects\\GenSyntaxParser\\src\\main\\java"));
        JProjectBuilder builder = new JProjectBuilder(
                "scripts/compile",
                "testJavaGen/syntaxParser");
        builder.excludePkg("template");
        loader = new SyntaxLoader();
        parser = new NFARegexParser();
        registerTokens();
        registerSyntax();
        DFABuilder.build(new LexerSourceGen(builder), parser);
        LRGroupBuilder.build(loader, new SyntaxAnalyzerSourceGen(builder));
        builder.gen();
    }

    private void registerTokens() {
        parser.register("@a+", "nonTerminal");
        parser.register("\\@@a+|'@.'", "terminal");
        parser.register("->", "point");
        parser.registerSingle('|', ';');
    }

    private void registerSyntax() {
        loader.begin("syntax")
                .addNonTerminal("production").finish();
        loader.begin("syntax")
                .addNonTerminal("syntax")
                .addNonTerminal("production")
                .finish();
        loader.begin("production")
                .addTerminal("@nt", 129)
                .addTerminal("@p", 130)
                .addNonTerminal("body")
                .addTerminal(';')
                .finish();
        loader.begin("body")
                .addNonTerminal("seq")
                .finish();
        loader.begin("body")
                .addNonTerminal("body")
                .addTerminal('|')
                .addNonTerminal("seq")
                .finish();
        loader.begin("seq").addNonTerminal("symbol").finish();
        loader.begin("seq").addNonTerminal("seq").addNonTerminal("symbol").finish();
        loader.begin("symbol").addTerminal("@nt", 129).finish();
        loader.begin("symbol").addTerminal("@t", 131).finish();
    }
}
