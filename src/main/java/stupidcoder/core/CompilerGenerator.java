package stupidcoder.core;

import stupidcoder.core.cpp.CLexerBuilder;
import stupidcoder.core.cpp.CSyntaxAnalyzerBuilder;
import stupidcoder.core.java.JLexerBuilder;
import stupidcoder.core.java.JSyntaxAnalyzerBuilder;
import stupidcoder.core.scriptloader.Lexer;
import stupidcoder.core.scriptloader.ScriptLoader;
import stupidcoder.lex.DFABuilder;
import stupidcoder.lex.NFARegexParser;
import stupidcoder.syntax.LRGroupBuilder;
import stupidcoder.syntax.SyntaxLoader;
import stupidcoder.util.Config;
import stupidcoder.util.generate.project.cpp.CProjectBuilder;
import stupidcoder.util.generate.project.java.JProjectBuilder;
import stupidcoder.util.input.CompilerInput;

public class CompilerGenerator {
    public static final int KEY_WORD_TOKEN = Config.register(Config.BOOL_T, false);

    public static void genJava(String scriptPath, String rootPkg) {
        try (CompilerInput input = CompilerInput.fromResource(Config.resourcePath(scriptPath))){
            JProjectBuilder builder = new JProjectBuilder("scripts/java", rootPkg);
            SyntaxLoader syntaxLoader = new SyntaxLoader();
            ScriptLoader scriptLoader = new ScriptLoader(syntaxLoader, new NFARegexParser());
            scriptLoader.run(new Lexer(input));
            builder.addAdapter(new JLexerBuilder(scriptLoader));
            builder.addAdapter(new JSyntaxAnalyzerBuilder(syntaxLoader));
            builder.excludePkg("template");
            builder.gen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void genJava(String scriptPath) {
        genJava(scriptPath, Config.getString(JProjectBuilder.FRIEND_PKG_PREFIX));
    }

    public static void genCpp(String scriptPath, String projectName) {
        try (CompilerInput input = CompilerInput.fromResource(Config.resourcePath(scriptPath))){
            SyntaxLoader syntaxLoader = new SyntaxLoader();
            ScriptLoader scriptLoader = new ScriptLoader(syntaxLoader, new NFARegexParser());
            scriptLoader.run(new Lexer(input));
            CProjectBuilder builder = new CProjectBuilder(projectName, "scripts/cpp");
            builder.addAdapter(new CLexerBuilder(scriptLoader));
            builder.addAdapter(new CSyntaxAnalyzerBuilder(syntaxLoader));
            builder.gen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enableSyntaxDebugInfo() {
        Config.set(LRGroupBuilder.SYNTAX_DEBUG_INFO, true);
    }

    public static void enableSyntaxConflictInfo() {
        Config.set(LRGroupBuilder.SYNTAX_CONFLICT_INFO, true);
    }

    public static void enableLexerDebugInfo() {
        Config.set(DFABuilder.LEXER_DEBUG_INFO, true);
    }

    public static void enableCompressedArr() {
        Config.set(JLexerBuilder.USE_COMPRESSED_ARR, true);
    }

    public static void enableKeyWordToken() {
        Config.set(KEY_WORD_TOKEN, true);
    }
}
