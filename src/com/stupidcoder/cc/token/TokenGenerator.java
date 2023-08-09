package com.stupidcoder.cc.token;

import com.stupidcoder.cc.util.generator.*;
import com.stupidcoder.cc.util.input.StringInput;

import java.util.ArrayList;
import java.util.List;

public class TokenGenerator extends AbstractWritableBuilder {
    private XClass clazzTokenWord;
    private XClass clazzTokenTypes;
    private XWritableList listInitKeyWord;
    private XWritableList listInitTokenTypes;
    private List<String> tokens = new ArrayList<>();
    private int maxTokenType = 127;

    public TokenGenerator() {
        clazzTokenWord = XClass.of("TokenWord", "tokens", "class", "implements IToken");
        clazzTokenTypes = XClass.of("TokenTypes");

        listInitKeyWord = new XWritableList().disableFinalLineBreak();
        listInitTokenTypes = new XWritableList().disableFinalLineBreak();
    }

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
        XClass clazzTokenSingle = XClass.fromFile("TokenSingle", "tokens", "tokens/single");
        generator.registerClazz(clazzIToken);
        generator.registerClazz(clazzTokenSingle);
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
        listInitTokenTypes.append("public static final int %s = %d;",
                tokenName.toUpperCase(),
                ++maxTokenType);
    }

    public static String clazzName(String tokenName) {
        StringBuilder sb = new StringBuilder();
        try (StringInput input = new StringInput(tokenName)) {
            while (input.available()) {
                sb.append(Character.toUpperCase((char)input.next()));
                input.markLexemeStart();
                while (input.available()) {
                    byte b = input.next();
                    if (b == '_') {
                        input.retract();
                        sb.append(input.lexeme());
                        input.next();
                        input.markLexemeStart();
                        break;
                    }
                }
                sb.append(input.lexeme());
            }
        }
        return "Token" + sb;
    }

    private static String srcPath(String tokenName) {
        return "tokens/" + tokenName;
    }
}
