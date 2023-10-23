package stupidcoder.core;

import stupidcoder.Config;
import stupidcoder.compile.lex.DFABuilder;
import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.compile.syntax.LRGroupBuilder;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.generate.generators.java.JProjectBuilder;
import stupidcoder.util.input.CompilerInput;

public class CompilerGenerator {
    private final JProjectBuilder builder;
    private final CompilerInput input;
    private final SyntaxLoader syntaxLoader;
    private final NFARegexParser parser;
    private final ScriptLoader loader;

    public static void gen(String scriptPath, String outputPath) {
        CompilerGenerator g = new CompilerGenerator(scriptPath, outputPath);
        try {
            g.gen();
        } catch (Exception e) {
            e.printStackTrace();
        }
        g.close();
    }

    private CompilerGenerator(String scriptPath, String outputPath) {
        this.builder = new JProjectBuilder("scripts/compile", outputPath);
        this.input = CompilerInput.fromResource(Config.resourcePath(scriptPath));
        this.syntaxLoader = new SyntaxLoader();
        this.parser = new NFARegexParser();
        this.loader = new ScriptLoader(syntaxLoader, parser);
    }

    private void gen() throws Exception {
        loader.run(new Lexer(input));
        DFABuilder.build(new SrcGenLexer(loader, builder), parser);
        LRGroupBuilder.build(syntaxLoader, new SrcGenSyntaxAnalyzer(builder));
        builder.excludePkg("template");
        builder.gen();
    }

    private void close() {
        input.close();
    }
}
