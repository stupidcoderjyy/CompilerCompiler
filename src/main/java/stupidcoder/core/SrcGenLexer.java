package stupidcoder.core;

import org.apache.commons.lang3.StringUtils;
import stupidcoder.Config;
import stupidcoder.compile.lex.IDfaSetter;
import stupidcoder.generate.generators.java.JProjectBuilder;
import stupidcoder.generate.sources.SourceCached;
import stupidcoder.generate.sources.SourceFieldInt;
import stupidcoder.generate.sources.arr.Source1DArrSetter;
import stupidcoder.generate.sources.arr.Source2DArrSetter;
import stupidcoder.generate.sources.arr.SourceArrSetter;
import stupidcoder.util.ArrayCompressor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SrcGenLexer implements IDfaSetter {
    private int statesCount, startState, goToSize, goToStartSize, goToOffsetsSize;
    private final Source1DArrSetter op;
    private final Source2DArrSetter goTo;
    private final Source1DArrSetter accepted;
    private final JProjectBuilder root;
    private final ScriptLoader loader;
    private final boolean compressUsed = Config.getBool(Config.COMPRESSED_ARR);
    private ArrayCompressor compressor = null;

    public SrcGenLexer(ScriptLoader loader, JProjectBuilder root) {
        this.loader = loader;
        this.root = root;
        this.goTo = new Source2DArrSetter("goTo", SourceArrSetter.FOLD_OPTIMIZE);
        this.op = new Source1DArrSetter("op",
                SourceArrSetter.FOLD_OPTIMIZE | SourceArrSetter.EXTRACT_COMMON_DATA);
        this.accepted = new Source1DArrSetter("accepted", SourceArrSetter.FOLD_OPTIMIZE);
        if (compressUsed) {
            this.compressor = new ArrayCompressor(new CompressedArrSetter(goTo) {
                @Override
                public void setSize(int dataSize, int startSize, int offsetsSize) {
                    goToSize = dataSize;
                    goToStartSize = startSize;
                    goToOffsetsSize = offsetsSize;
                }
            });
            root.addClazzImport("Lexer", "ArrayCompressor");
        }
        root.registerClazzSrc("Lexer",
                new SourceFieldInt("fStatesCount", () -> statesCount),
                new SourceFieldInt("fStartState", () -> startState),
                new SourceFieldInt("dataSize", () -> goToSize),
                new SourceFieldInt("startSize", () -> goToStartSize),
                new SourceFieldInt("offsetsSize", () -> goToOffsetsSize),
                new SourceFieldInt("compressUsed", () -> compressUsed ? 0 : 1),
                goTo,
                op,
                accepted);
    }

    public SrcGenLexer(JProjectBuilder root) {
        this(null, root);
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
        if (token.equals("id") && Config.getBool(Config.KEY_WORD)) {
            root.registerClazz("stupidcoder.compile.tokens.TokenId", "stupidcoder/template/$TokenId.java");
            SourceCached srcKeyWords = new SourceCached("keyWords");
            loader.keyWords.forEach((name, id) -> {
                srcKeyWords.writeString(name);
                srcKeyWords.writeInt(id);
            });
            root.registerClazzSrc("TokenId",
                    srcKeyWords,
                    new SourceFieldInt("id", () -> loader.nameToTerminalId.get("id")));
            return;
        }
        String name = "Token" + StringUtils.capitalize(token);
        root.registerClazz("stupidcoder.compile.tokens." + name, "stupidcoder/template/$Token.java");
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
