package com.stupidcoder.cc.lex;

import com.stupidcoder.cc.lex.core.DFABuilder;
import com.stupidcoder.cc.lex.core.IDfaSetter;
import com.stupidcoder.cc.lex.core.NFARegexParser;
import com.stupidcoder.cc.token.TokenGenerator;
import com.stupidcoder.cc.util.generator.*;

public class DfaGenerator implements IWritableBuilder, IDfaSetter {
    private XClass clazzDfa = XClass.of("DFA", "");
    private X2DArraySetter goToSetter = new X2DArraySetter("goTo");
    private XWritableList listInitAccepted = new XWritableList();
    private XArraySetter tokensSetter = new XArraySetter("tokens", "IToken", "t");
    private NFARegexParser parser = new NFARegexParser();
    private int statesCount;

    public void register(String regex, String tokenName) {
        parser.register(regex, tokenName);
    }

    public void registerSingle(String regex) {
        parser.register(regex, "single");
    }

    @Override
    public void init() {
        DFABuilder.build(this, parser);
        goToSetter.finish();
        tokensSetter.finish();

        XFunction funcConstructor = XFunction.of("public DFA")
                .arg("ILexerInput", "input")
                .addContent("this.input = input;")
                .addContent("input.open();")
                .addContent("goTo = new int[%d][%d];", statesCount, 128)
                .addContent("accepted = new boolean[%d];", statesCount)
                .addContent("tokens = new IToken[%d];", statesCount)
                .addContent("init();");
        XFunction funcInit = XFunction.of("private void init")
                .addContent(goToSetter)
                .addContent(listInitAccepted)
                .addContent(tokensSetter);

        clazzDfa.addInternalImport("tokens.*")
                .addInternalImport("util.input.ILexerInput")
                .field(XFile.of("lex/dfa_fields"))
                .function(funcConstructor)
                .function(funcInit)
                .function(XFile.of("lex/dfa_run"));
    }

    @Override
    public void build(CodeWriter generator) {
        generator.registerClazz(clazzDfa);
        generator.output();
    }

    @Override
    public void clear() {
        clazzDfa = null;
        goToSetter = null;
        listInitAccepted = null;
        tokensSetter = null;
        parser = null;
    }

    @Override
    public void setAccepted(int i) {
        listInitAccepted.append("accepted[%d] = true;", i);
    }

    @Override
    public void setGoTo(int start, byte input, int target) {
        goToSetter.set(start, input, target);
    }

    @Override
    public void setStartState(int i) {
        clazzDfa.field("private static final int startState = " + i + ";");
    }

    @Override
    public void setToken(int i, String token) {
        tokensSetter.set(i, "new " + TokenGenerator.clazzName(token) + "()");
    }

    @Override
    public void setDfaStatesCount(int count) {
        statesCount = count;
    }
}
