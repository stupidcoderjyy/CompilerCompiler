package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.compile.lex.DFABuilder;
import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.core.SrcGenLexer;
import stupidcoder.generate.generators.java.JProjectBuilder;

public class TestScriptLoaderGen {

    @Test
    public void test() {
//        CompilerGenerator.gen("generate/compiler/scriptloader.txt", "");
        JProjectBuilder builder = new JProjectBuilder("scripts/compile", "");
        NFARegexParser parser = new NFARegexParser();
        parser.register("@a@u*", "id");
        parser.register("\\\"([^\"]|\\\\\")*\\\"", "string");
        parser.register("$$", "blockEnd");
        parser.register("$syntax$", "syntaxBegin");
        parser.register("$token$", "tokenBegin");
        parser.register("\\@($@a+|@a+|~|$$)|'@.'", "terminal");
        parser.register("%@d+", "priorityMarkProd");
        parser.register("$@d+", "priorityMarkTerminal");
        parser.register("->", "point");
        parser.register("$end$", "endHead");
        parser.registerSingle(';', '|', ':');
        DFABuilder.build(new SrcGenLexer(null, builder), parser);
        builder.excludePkg("template");
        builder.gen();
    }
}
