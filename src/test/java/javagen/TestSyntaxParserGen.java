package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.Config;
import stupidcoder.PathUtil;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.compile.lex.DFABuilder;
import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.compile.syntax.LRGroupBuilder;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.SrcGenLexer;
import stupidcoder.core.SrcGenSyntaxAnalyzer;
import stupidcoder.generate.generators.java.JProjectBuilder;

public class TestSyntaxParserGen {
    private SyntaxLoader loader;
    private NFARegexParser parser;

    @Test
    public void test() {
        Config.set(Config.OUTPUT_DIR, PathUtil.desktopPath("cs\\IdeaProjects\\GenSyntaxParser\\src\\main\\java"));
        JProjectBuilder builder = new JProjectBuilder(
                "scripts/compile",
                "");
        builder.excludePkg("template");
        loader = new SyntaxLoader();
        parser = new NFARegexParser();
        registerTokens();
        registerSyntax();
        DFABuilder.build(new SrcGenLexer(builder), parser);
        LRGroupBuilder.build(loader, new SrcGenSyntaxAnalyzer(builder));
        builder.gen();
    }

    private void registerTokens() {
        parser.register("@a+", "nonTerminal");
        parser.register("\\@@a+|'@.'", "terminal");
        parser.register("->", "point");
        parser.registerSingle('|', ';');
        parser.register("%@d+", "priorityMark");
    }

    private void registerSyntax() {
        loader.begin("syntax")
                .addNonTerminal("production")
                .finish();
        loader.begin("syntax")
                .addNonTerminal("syntax")
                .addNonTerminal("production")
                .finish();
        loader.begin("production")
                .addTerminal("@nt", 132)
                .addTerminal("@p", 130)
                .addNonTerminal("body")
                .addTerminal(';')
                .finish();
        loader.begin("body")
                .addNonTerminal("slice")
                .finish();
        loader.begin("body")
                .addNonTerminal("body")
                .addTerminal('|')
                .addNonTerminal("slice")
                .finish();
        loader.begin("slice")
                .addNonTerminal("seq")
                .addNonTerminal("priority")
                .finish();
        loader.begin("priority")
                .add(DefaultSymbols.EPSILON)
                .finish();
        loader.begin("priority")
                .addTerminal("@pMark", 131)
                .finish();
        loader.begin("seq")
                .addNonTerminal("symbol")
                .finish();
        loader.begin("seq")
                .addNonTerminal("seq")
                .addNonTerminal("symbol")
                .finish();
        loader.begin("symbol")
                .addTerminal("@nt", 132)
                .finish();
        loader.begin("symbol")
                .addTerminal("@t", 129)
                .finish();
    }
}
