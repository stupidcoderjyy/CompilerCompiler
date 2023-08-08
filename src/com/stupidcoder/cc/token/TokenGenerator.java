package com.stupidcoder.cc.token;

import com.stupidcoder.cc.common.Config;
import com.stupidcoder.cc.util.generator.*;

import java.util.ArrayList;
import java.util.List;

public class TokenGenerator extends AbstractWritableBuilder {
    private XClass clazzTokenWord = XClass.of(
            "TokenWord", "tokens", "class", "implements IToken");
    private XWritableList listInitKeyWord = new XWritableList().disableFinalLineBreak();
    private XClass clazzTokenTypes = XClass.of("TokenTypes");
    private XWritableList listInitTokenTypes = new XWritableList().disableFinalLineBreak();
    private List<String> tokens = new ArrayList<>();
    private int maxTokenType = 127;

    public void registerKeyWord(String keyWord) {
        listInitKeyWord.append("keyWords.put(\"%s\", TokenTypes.KEYWORD_%s);", keyWord, keyWord.toUpperCase());
        registerTokenType("KEYWORD_" + keyWord);
    }

    public void registerToken(String tokenName) {
        tokens.add(tokenName);
        registerTokenType(tokenName);
    }

    @Override
    protected void init() {
        clazzTokenWord.field(XFile.of("tokens/word"))
                .function(XFunction.of("private static void init").addContent(listInitKeyWord))
                .addInternalImport("IToken")
                .addInternalImport("TokenTypes")
                .add3rdPartyImport("java.util.Map")
                .add3rdPartyImport("java.util.HashMap");
        clazzTokenTypes.field(listInitTokenTypes);
    }

    @Override
    protected void buildTarget(CodeWriter generator) {
        XClass clazzIToken = XClass.fromFile("IToken", "", "lex/IToken");
        generator.registerClazz(clazzIToken);
        for (String token : tokens) {
            XClass clazz = XClass.fromFile(clazzName(token), "tokens", srcPath(token));
            clazz.addInternalImport("IToken").addInternalImport("TokenTypes");
            generator.registerClazz(clazz);
        }
        generator.registerClazz(clazzTokenWord);
        generator.registerClazz(clazzTokenTypes);
        generator.output();
    }

    @Override
    protected void clear() {
        clazzTokenTypes = null;
        clazzTokenWord = null;
        listInitTokenTypes = null;
        listInitKeyWord = null;
        tokens = null;
    }

    private void registerTokenType(String tokenName) {
        listInitTokenTypes.append("public static final int %s = %d;", tokenName.toUpperCase(), ++maxTokenType);
    }

    public static String clazzName(String tokenName) {
        return "Token" + tokenName.substring(0, 1).toUpperCase() +
                tokenName.substring(1);
    }

    private static String clazzFileName(String tokenName) {
        return clazzName(tokenName) + ".java";
    }

    private static String srcPath(String tokenName) {
        return "tokens/" + tokenName;
    }
}
