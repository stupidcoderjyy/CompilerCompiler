package stupidcoder.core;

import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.sctiptloader.Lexer;
import stupidcoder.core.sctiptloader.ScriptLoader;
import stupidcoder.util.Config;
import stupidcoder.util.generate.project.java.JProjectBuilder;
import stupidcoder.util.input.CompilerInput;

public class CompilerGenerator {
    public static final int SYNTAX_DEBUG_INFO = Config.register(Config.BOOL_T, false);
    public static final int SYNTAX_CONFLICT_INFO = Config.register(Config.BOOL_T, false);
    public static final int LEXER_DEBUG_INFO = Config.register(Config.BOOL_T, false);
    public static final int USE_COMPRESSED_ARR = Config.register(Config.BOOL_T, false);
    public static final int KEY_WORD_TOKEN = Config.register(Config.BOOL_T, false);

    public static void gen(String scriptPath, String rootPkg, String friendPkgPrefix) {
        try (CompilerInput input = CompilerInput.fromResource(Config.resourcePath(scriptPath))){
            JProjectBuilder builder = new JProjectBuilder("scripts/compile", rootPkg, friendPkgPrefix);
            SyntaxLoader syntaxLoader = new SyntaxLoader();
            ScriptLoader scriptLoader = new ScriptLoader(syntaxLoader, new NFARegexParser());
            scriptLoader.run(new Lexer(input));
            builder.addAdapter(new LexerBuilder(scriptLoader));
            builder.addAdapter(new SyntaxAnalyzerBuilder(syntaxLoader));
            builder.excludePkg("template");
            builder.gen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gen(String scriptPath, String rootPkg) {
        try (CompilerInput input = CompilerInput.fromResource(Config.resourcePath(scriptPath))){
            JProjectBuilder builder = new JProjectBuilder("scripts/compile", rootPkg);
            SyntaxLoader syntaxLoader = new SyntaxLoader();
            ScriptLoader scriptLoader = new ScriptLoader(syntaxLoader, new NFARegexParser());
            scriptLoader.run(new Lexer(input));
            builder.addAdapter(new LexerBuilder(scriptLoader));
            builder.addAdapter(new SyntaxAnalyzerBuilder(syntaxLoader));
            builder.excludePkg("template");
            builder.gen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gen(String scriptPath) {
        gen(scriptPath, Config.getString(JProjectBuilder.FRIEND_PKG_PREFIX));
    }

    public static void enableSyntaxDebugInfo() {
        Config.set(SYNTAX_DEBUG_INFO, true);
    }

    public static void enableSyntaxConflictInfo() {
        Config.set(SYNTAX_CONFLICT_INFO, true);
    }

    public static void enableLexerDebugInfo() {
        Config.set(LEXER_DEBUG_INFO, true);
    }

    public static void enableCompressedArr() {
        Config.set(USE_COMPRESSED_ARR, true);
    }

    public static void enableKeyWordToken() {
        Config.set(KEY_WORD_TOKEN, true);
    }
}
