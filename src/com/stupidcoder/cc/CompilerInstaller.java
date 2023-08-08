package com.stupidcoder.cc;

import com.stupidcoder.cc.lex.DfaGenerator;
import com.stupidcoder.cc.token.TokenGenerator;

public class CompilerInstaller {
    private final TokenGenerator tokenGenerator;
    private final DfaGenerator dfaGenerator;

    public CompilerInstaller() {
        tokenGenerator = new TokenGenerator();
        dfaGenerator = new DfaGenerator();
    }

    public CompilerInstaller registerToken(String regex, String tokenName) {
        dfaGenerator.register(regex, tokenName);
        tokenGenerator.registerToken(tokenName);
        return this;
    }

    public CompilerInstaller registerKeyWord(String keyWord) {
        tokenGenerator.registerKeyWord(keyWord);
        return this;
    }

    public void build() {
        tokenGenerator.genDefault();
        dfaGenerator.genDefault();
    }
}
