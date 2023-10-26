package stupidcoder.core;

import org.apache.commons.lang3.StringUtils;
import stupidcoder.compile.lex.DFABuilder;
import stupidcoder.compile.lex.IDfaSetter;
import stupidcoder.core.sctiptloader.ScriptLoader;
import stupidcoder.util.Config;
import stupidcoder.util.arrcompressor.ArrayCompressor;
import stupidcoder.util.generate.project.java.IJavaProjectAdapter;
import stupidcoder.util.generate.project.java.JProjectBuilder;
import stupidcoder.util.generate.sources.SourceCached;
import stupidcoder.util.generate.sources.SourceFieldInt;
import stupidcoder.util.generate.sources.arr.CompressedArrSourceSetter;
import stupidcoder.util.generate.sources.arr.Source1DArrSetter;
import stupidcoder.util.generate.sources.arr.Source2DArrSetter;
import stupidcoder.util.generate.sources.arr.SourceArrSetter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LexerBuilder implements IDfaSetter, IJavaProjectAdapter {
    private int statesCount, startState, goToSize, goToStartSize, goToOffsetsSize;
    private final Source1DArrSetter op;
    private final Source2DArrSetter goTo;
    private final Source1DArrSetter accepted;
    private final ScriptLoader loader;
    private final boolean compressUsed = Config.getBool(CompilerGenerator.USE_COMPRESSED_ARR);
    private final boolean keyWordEnabled = Config.getBool(CompilerGenerator.KEY_WORD_TOKEN);
    private ArrayCompressor compressor = null;
    private JProjectBuilder root;

    public LexerBuilder(ScriptLoader loader) {
        this.loader = loader;
        this.goTo = new Source2DArrSetter("goTo", SourceArrSetter.FOLD_OPTIMIZE);
        this.op = new Source1DArrSetter("op",
                SourceArrSetter.FOLD_OPTIMIZE | SourceArrSetter.EXTRACT_COMMON_DATA);
        this.accepted = new Source1DArrSetter("accepted", SourceArrSetter.FOLD_OPTIMIZE);
        if (compressUsed) {
            this.compressor = new ArrayCompressor(new CompressedArrSourceSetter(goTo) {
                @Override
                public void setSize(int dataSize, int startSize, int offsetsSize) {
                    goToSize = dataSize;
                    goToStartSize = startSize;
                    goToOffsetsSize = offsetsSize;
                }
            });
        }
    }

    @Override
    public void build(JProjectBuilder builder) {
        this.root = builder;
        DFABuilder.build(this, loader.regexParser);
        if (compressUsed) {
            builder.addClazzInternalImport("Lexer", "ArrayCompressor");
        }
        builder.registerClazzSrc("Lexer",
                new SourceFieldInt("fStatesCount", () -> statesCount),
                new SourceFieldInt("fStartState", () -> startState),
                new SourceFieldInt("dataSize", () -> goToSize),
                new SourceFieldInt("startSize", () -> goToStartSize),
                new SourceFieldInt("offsetsSize", () -> goToOffsetsSize),
                new SourceFieldInt("compressUsed", () -> compressUsed ? 0 : 1),
                goTo,
                op,
                accepted);
        builder.addClazzPkgImport("Lexer", "compile.tokens");
    }

    @Override
    public void setAccepted(int i, String token) {
        String tokenName = StringUtils.capitalize(token);
        op.set(i, tokenName);
        accepted.set(i, "true");
    }

    @Override
    public void setGoTo(int start, int input, int target) {
        if (compressUsed) {
            compressor.set(start, input, String.valueOf(target));
        } else {
            goTo.set(start, input, target);
        }
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
            setTokenFile(token);
            added.add(token);
        }
        if (compressUsed) {
            compressor.finish();
        }
    }

    private void setTokenFile(String token) {
        if (token.equals("id") && keyWordEnabled) {
            root.registerClazz("compile.tokens.TokenId", "template/$TokenId.java");
            SourceCached srcKeyWords = new SourceCached("keyWords");
            loader.keyWords.forEach((name, id) -> {
                srcKeyWords.writeString(name);
                srcKeyWords.writeInt(id);
            });
            root.registerClazzSrc("TokenId",
                    srcKeyWords,
                    new SourceFieldInt("id", () -> loader.nameToTerminalId.computeIfAbsent("id", n -> 0)));
            return;
        }
        String name = "Token" + StringUtils.capitalize(token);
        root.registerClazz("compile.tokens." + name, "template/$Token.java");
        SourceCached srcName = new SourceCached("name");
        SourceCached srcId = new SourceCached("id");
        srcName.writeString(name);
        if (loader == null) {
            srcId.writeInt(0);
        } else if (loader.nameToTerminalId.containsKey(token)) {
            srcId.writeInt(loader.nameToTerminalId.get(token));
        }
        root.registerClazzSrc(name, srcName, srcId);
    }
}
