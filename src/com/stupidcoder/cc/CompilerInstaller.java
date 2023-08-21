package com.stupidcoder.cc;

import com.stupidcoder.cc.lex.DfaGenerator;
import com.stupidcoder.cc.token.TokenGenerator;
import com.stupidcoder.cc.util.generator.CodeWriter;
import com.stupidcoder.cc.util.generator.XClass;

public class CompilerInstaller {
    private final TokenGenerator tokenGenerator = new TokenGenerator();
    private final DfaGenerator dfaGenerator = new DfaGenerator();

    public CompilerInstaller() {
        registerToken("@a@w*", "word"); //默认安装
    }

    public CompilerInstaller registerToken(String regex, String tokenName) {
        dfaGenerator.register(regex, tokenName);
        tokenGenerator.registerToken(tokenName);
        return this;
    }

    public CompilerInstaller registerSingleToken(String regex) {
        dfaGenerator.registerSingle(regex);
        return this;
    }

    public CompilerInstaller registerKeyWord(String keyWord) {
        tokenGenerator.registerKeyWord(keyWord);
        return this;
    }

    public void build() {
        CodeWriter writer = new CodeWriter(Config.OUTPUT_ROOT, Config.OUTPUT_ROOT_PACKAGE);
        tokenGenerator.gen(writer);
        dfaGenerator.gen(writer);
        XClass clazzIInput = XClass.fromFile("ILexerInput", "util.input", "util/ILexerInput");
        XClass clazzStringInput = XClass.fromFile("StringInput", "util.input", "util/StringInput");
        XClass clazzIToken = XClass.fromFile("IToken", "", "lex/IToken");
        writer.registerClazz(clazzIToken);
        writer.registerClazz(clazzIInput);
        writer.registerClazz(clazzStringInput);
        writer.output();
        CodeWriter mainWriter = new CodeWriter(Config.OUTPUT_ROOT, "");
        XClass clazzMain = XClass.fromFile("Main", "", "main")
                .addInternalImport("com.stupidcoder.generated.DFA")
                .addInternalImport("com.stupidcoder.generated.util.input.StringInput");
        mainWriter.registerClazz(clazzMain);
        mainWriter.output();
    }
}
