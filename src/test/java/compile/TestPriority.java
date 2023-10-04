package compile;

import org.junit.jupiter.api.Test;
import stupidcoder.Config;
import stupidcoder.PathUtil;
import stupidcoder.compile.lex.DFABuilder;
import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.compile.syntax.LRGroupBuilder;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.SrcGenLexer;
import stupidcoder.core.SrcGenSyntaxAnalyzer;
import stupidcoder.generate.generators.java.JProjectBuilder;

public class TestPriority {
    private SyntaxLoader loader;
    private NFARegexParser parser;

    @Test
    public void test() {
        Config.set(Config.OUTPUT_DIR, PathUtil.desktopPath("cs\\IdeaProjects\\GenCaculator\\src\\main\\java"));
        JProjectBuilder builder = new JProjectBuilder("scripts/compile", "");
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
        parser.register("@d+", "num");
        parser.registerSingle('+');
    }

    private void registerSyntax() {
        loader.begin("expr")
                .addNonTerminal("expr")
                .addTerminal('+')
                .addNonTerminal("expr")
                .finish();
        loader.begin("expr")
                .addTerminal("@num", 12)
                .finish();
        loader.begin("expr")
                .addTerminal('+')
                .addNonTerminal("expr")
                .finish(10);
    }
}
