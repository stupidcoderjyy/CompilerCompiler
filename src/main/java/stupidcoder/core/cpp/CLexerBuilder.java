package stupidcoder.core.cpp;

import org.apache.commons.lang3.StringUtils;
import stupidcoder.core.CompilerGenerator;
import stupidcoder.core.scriptloader.ScriptLoader;
import stupidcoder.lex.DFABuilder;
import stupidcoder.lex.IDfaSetter;
import stupidcoder.util.Config;
import stupidcoder.util.generate.Generator;
import stupidcoder.util.generate.project.cpp.CProjectBuilder;
import stupidcoder.util.generate.project.cpp.ICppProjectAdapter;
import stupidcoder.util.generate.sources.SourceCached;
import stupidcoder.util.generate.sources.SourceFieldInt;
import stupidcoder.util.generate.sources.arr.Source1DArrSetter;
import stupidcoder.util.generate.sources.arr.Source2DArrSetter;
import stupidcoder.util.generate.sources.arr.SourceArrSetter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CLexerBuilder implements IDfaSetter, ICppProjectAdapter {
    private final ScriptLoader loader;
    private int tokensCount;
    private int statesCount;
    private int startState;
    private final SourceCached srcTokenDef = new SourceCached("tokenDef");
    private final SourceCached srcTokenImpl = new SourceCached("tokenImpl");
    private final SourceFieldInt srcTokensCountHeader = new SourceFieldInt("tokensCount", () -> tokensCount);
    private final SourceFieldInt srcTokensCountCpp = new SourceFieldInt("cppTokensCount", () -> tokensCount);
    private final SourceFieldInt srcStatesCount = new SourceFieldInt("statesCount", () -> statesCount);
    private final SourceFieldInt srcStartState = new SourceFieldInt("startState", () -> startState);
    private final Source1DArrSetter op = new Source1DArrSetter("op",
            SourceArrSetter.FOLD_OPTIMIZE | SourceArrSetter.EXTRACT_COMMON_DATA);
    private final Source2DArrSetter goTo = new Source2DArrSetter("goTo", SourceArrSetter.FOLD_OPTIMIZE);
    private final Source1DArrSetter accepted = new Source1DArrSetter("accepted", SourceArrSetter.FOLD_OPTIMIZE);
    private final boolean keyWordEnabled = Config.getBool(CompilerGenerator.KEY_WORD_TOKEN);
    private final SourceFieldInt srcKeyWordEnabled = new SourceFieldInt("keyWordEnabled", () -> keyWordEnabled ? 1 : 0);
    private final SourceCached srcTokenIdDef = new SourceCached("tokenIdDef");

    public CLexerBuilder(ScriptLoader loader) {
        this.loader = loader;
    }

    @Override
    public void setAccepted(int i, String token) {
        String tokenName = StringUtils.capitalize(token);
        op.set(i, tokenName);
        accepted.set(i, "true");
    }

    @Override
    public void setGoTo(int start, int input, int target) {
        goTo.set(start, input, target);
    }

    @Override
    public void setStartState(int i) {
        this.startState = i;
    }

    @Override
    public void setDfaStatesCount(int count) {
        this.statesCount = count;
    }

    @Override
    public void setOthers(List<String> tokens) {
        Set<String> added = new HashSet<>();
        for (String token : tokens) {
            if (token == null || added.contains(token) || token.equals("single")) {
                continue;
            }
            added.add(token);
            setToken(token);
        }
    }

    private void setToken(String token) {
        if(token.equals("id") && keyWordEnabled) {
            srcTokenIdDef.writeInt(loader.nameToTerminalId.computeIfAbsent("id", n -> 0));
            loader.keyWords.forEach((name, id) -> {
                srcTokenIdDef.writeString(name);
                srcTokenIdDef.writeInt(id);
            });
            return;
        }
        tokensCount++;
        //h
        String name = "Token" + StringUtils.capitalize(token);
        srcTokenDef.writeString(name);
        if (loader == null) {
            srcTokenDef.writeInt(0);
        } else {
            srcTokenDef.writeInt(loader.nameToTerminalId.getOrDefault(token, 0));
        }
        //cpp
        srcTokenImpl.writeString(name);
    }

    @Override
    public void build(CProjectBuilder builder) {
        DFABuilder.build(this, loader.regexParser);

        Generator headerFile = new Generator();
        headerFile.registerSrc(srcTokenDef);
        headerFile.registerSrc(srcTokensCountHeader);
        headerFile.registerSrc(srcKeyWordEnabled);
        if (keyWordEnabled) {
            headerFile.registerSrc(srcTokenIdDef);
        }

        Generator sourceFile = new Generator();
        sourceFile.registerSrc(srcStatesCount);
        sourceFile.registerSrc(goTo);
        sourceFile.registerSrc(accepted);
        sourceFile.registerSrc(op);
        sourceFile.registerSrc(srcStartState);
        sourceFile.registerSrc(srcTokensCountCpp);
        sourceFile.registerSrc(srcTokenImpl);
        sourceFile.registerSrc(srcKeyWordEnabled);

        builder.include("Lexer.h", headerFile);
        builder.include("Lexer.cpp", sourceFile);
    }
}
