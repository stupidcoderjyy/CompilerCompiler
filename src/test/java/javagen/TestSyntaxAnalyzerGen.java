package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.compile.syntax.LRGroupBuilder;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.SrcGenSyntaxAnalyzer;
import stupidcoder.generate.generators.java.JProjectBuilder;

public class TestSyntaxAnalyzerGen {

    @Test
    public void test() {
        JProjectBuilder builder = new JProjectBuilder("scripts/compile", "syntaxAnalyzerGen");
        builder.excludeClazz("Lexer");
        builder.excludePkg("template");
        SyntaxLoader loader = new SyntaxLoader();
        loader.begin("S")
                .addNonTerminal("L")
                .addTerminal('=')
                .addNonTerminal("R")
                .finish();
        loader.begin("S")
                .addNonTerminal("R")
                .finish();
        loader.begin("L")
                .addTerminal('*')
                .addNonTerminal("R")
                .finish();
        loader.begin("L")
                .addTerminal("id", 128)
                .finish();
        loader.begin("R")
                .addNonTerminal("L")
                .finish();
        LRGroupBuilder.build(loader, new SrcGenSyntaxAnalyzer(builder));
        builder.gen();
    }
}
