package stupidcoder.core;

import stupidcoder.Config;
import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.sctiptloader.Lexer;
import stupidcoder.core.sctiptloader.ScriptLoader;
import stupidcoder.generate.project.java.JProjectBuilder;
import stupidcoder.util.input.CompilerInput;

public class CompilerGenerator {
    private final JProjectBuilder builder;
    private final CompilerInput input;
    private final SyntaxLoader syntaxLoader;
    private final ScriptLoader loader;

    public static void gen(String scriptPath, String rootPkg) {
        CompilerGenerator g = new CompilerGenerator(scriptPath, rootPkg);
        try {
            g.gen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gen(String scriptPath) {
        gen(scriptPath, Config.getString(Config.GEN_PKG_PREFIX));
    }

    private CompilerGenerator(String scriptPath, String rootPkg) {
        this.builder = new JProjectBuilder("scripts/compile", rootPkg);
        this.input = CompilerInput.fromResource(Config.resourcePath(scriptPath));
        this.syntaxLoader = new SyntaxLoader();
        this.loader = new ScriptLoader(syntaxLoader, new NFARegexParser());
    }

    private void gen() throws Exception {
        loader.run(new Lexer(input));
        builder.addBuilder(new LexerBuilder(loader));
        builder.addBuilder(new SyntaxAnalyzerBuilder(syntaxLoader));
        builder.excludePkg("template");
        builder.gen();
    }
}
