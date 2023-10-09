package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.Config;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.compile.lex.DFABuilder;
import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.SrcGenLexer;
import stupidcoder.generate.generators.java.JProjectBuilder;

public class TestScriptLoaderGen {
    private SyntaxLoader loader;
    private NFARegexParser parser;

    @Test
    public void test() {
//        Config.set(Config.OUTPUT_DIR, PathUtil.desktopPath("cs\\IdeaProjects\\TestCodeGen\\src"));
        JProjectBuilder builder = new JProjectBuilder("scripts/compile", "testJavaGen/varParser");
        Config.set(Config.COMPRESSED_ARR, true);
        builder.excludePkg("template");
        loader = new SyntaxLoader();
        parser = new NFARegexParser();
        registerTokens();
//        registerSyntax();
        DFABuilder.build(new SrcGenLexer(builder), parser);
//        LRGroupBuilder.build(loader, new SrcGenSyntaxAnalyzer(builder));
        builder.gen();
    }

    private void registerTokens() {
        parser.register("@a+", "id");
        parser.register("\"", "string");
        parser.register("$$", "blockEnd");
        parser.register("$syntax$", "syntaxBegin");
        parser.register("$token$", "tokenBegin");
        parser.register("\\@($@a+|@a+|~)|'@.'", "terminal");
        parser.register("%@d+", "priorityMarkProd");
        parser.register("$@d+", "priorityMarkTerminal");
        parser.register("->", "point");
        parser.registerSingle(':', ';', '|');
    }

    private void registerSyntax() {
        loader.begin("script")
                .addNonTerminal("block")
                .finish();
        loader.begin("script")
                .addNonTerminal("script")
                .addNonTerminal("block")
                .finish();
        loader.begin("block")
                .addNonTerminal("content")
                .addTerminal("blockEnd", 128)
                .finish();
        loader.begin("content")
                .addTerminal("syntaxBegin", 129)
                .addNonTerminal("syntax")
                .finish();
        loader.begin("content")
                .addTerminal("tokenBegin", 130)
                .addNonTerminal("tokens")
                .finish();

        loader.begin("syntax")
                .addNonTerminal("production").finish();
        loader.begin("syntax")
                .addNonTerminal("syntax")
                .addNonTerminal("production")
                .finish();
        loader.begin("production")
                .addNonTerminal("head")
                .addTerminal("point", 132)
                .addNonTerminal("body")
                .addTerminal(';')
                .finish();
        loader.begin("head")
                .addTerminal("id", 131)
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
                .addNonTerminal("priorityP")
                .finish();
        loader.begin("priorityP")
                .add(DefaultSymbols.EPSILON)
                .finish();
        loader.begin("priorityP")
                .addTerminal("pMarkP", 133)
                .finish();
        loader.begin("seq")
                .addNonTerminal("symbol")
                .finish();
        loader.begin("seq")
                .addNonTerminal("seq")
                .addNonTerminal("symbol")
                .finish();
        loader.begin("symbol")
                .addTerminal("id", 131)
                .finish();
        loader.begin("symbol")
                .addTerminal("terminal", 134)
                .addNonTerminal("priorityT")
                .finish();
        loader.begin("priorityT")
                .add(DefaultSymbols.EPSILON)
                .finish();
        loader.begin("priorityT")
                .addTerminal("pMarkT", 135)
                .finish();

        loader.begin("tokens")
                .addNonTerminal("token")
                .finish();
        loader.begin("tokens")
                .addNonTerminal("tokens")
                .addNonTerminal("token")
                .finish();
        loader.begin("token")
                .addTerminal("id", 131)
                .addTerminal(':')
                .addTerminal("string", 136)
                .addTerminal(';')
                .finish();
    }
}
