package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.compile.lex.DFABuilder;
import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.compile.syntax.LRGroupBuilder;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.SrcGenLexer;
import stupidcoder.core.SrcGenSyntaxAnalyzer;
import stupidcoder.generate.generators.java.JProjectBuilder;

public class TestVarParserGen {
    private SyntaxLoader loader;
    private NFARegexParser parser;

    @Test
    public void test() {
//        Config.set(Config.OUTPUT_DIR, PathUtil.desktopPath("cs\\IdeaProjects\\TestCodeGen\\src"));
        JProjectBuilder builder = new JProjectBuilder("scripts/compile", "testJavaGen/varParser");
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
        parser.register("@a@w*", "id");
        parser.register("\"", "string");
        parser.registerSingle(':', ';', '[', ']', ',');
    }

    private void registerSyntax() {
        loader.begin("stmts")
                .addNonTerminal("stmt")
                .finish();
        loader.begin("stmts")
                .addNonTerminal("stmts")
                .addNonTerminal("stmt")
                .finish();
        loader.begin("stmt")
                .addTerminal("id", 128)
                .addTerminal(':')
                .addNonTerminal("val")
                .addTerminal(';')
                .finish();
        loader.begin("val")
                .addTerminal("string", 129)
                .finish();
        loader.begin("val")
                .addNonTerminal("list")
                .finish();
        loader.begin("list")
                .addTerminal('[')
                .addNonTerminal("content")
                .addTerminal(']')
                .finish();
        loader.begin("content")
                .add(DefaultSymbols.EPSILON)
                .finish();
        loader.begin("content")
                .addNonTerminal("strings")
                .finish();
        loader.begin("strings")
                .addTerminal("string", 129)
                .finish();
        loader.begin("strings")
                .addNonTerminal("strings")
                .addTerminal(',')
                .addTerminal("string", 129)
                .finish();
    }
}
