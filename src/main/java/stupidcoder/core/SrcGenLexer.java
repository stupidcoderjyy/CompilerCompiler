package stupidcoder.core;

import org.apache.commons.lang3.StringUtils;
import stupidcoder.compile.lex.IDfaSetter;
import stupidcoder.generate.generators.java.JProjectBuilder;
import stupidcoder.generate.sources.SourceCached;
import stupidcoder.generate.sources.SourceFieldInt;
import stupidcoder.generate.sources.arr.Source1DArrSetter;
import stupidcoder.generate.sources.arr.SourceArrSetter;
import stupidcoder.util.ArrayCompressor;
import stupidcoder.util.ICompressedArraySetter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SrcGenLexer implements IDfaSetter, ICompressedArraySetter {
    private int statesCount, startState, dataSize, startSize, offsetsSize;
    private final Source1DArrSetter op, goTo, start, offsets;
    private final SourceCached accepted;
    private final JProjectBuilder root;
    private final ScriptLoader loader;
    private final ArrayCompressor compressor;

    public SrcGenLexer(ScriptLoader loader, JProjectBuilder root) {
        this.loader = loader;
        this.root = root;
        this.goTo = new Source1DArrSetter("goTo", SourceArrSetter.FOLD_OPTIMIZE);
        this.start = new Source1DArrSetter("start", SourceArrSetter.FOLD_OPTIMIZE);
        this.offsets = new Source1DArrSetter("offsets", SourceArrSetter.FOLD_OPTIMIZE);
        this.op = new Source1DArrSetter("op",
                SourceArrSetter.FOLD_OPTIMIZE | SourceArrSetter.EXTRACT_COMMON_DATA);
        this.compressor = new ArrayCompressor(this);
        this.accepted = new SourceCached("accepted");
        root.registerClazzSrc("Lexer",
                new SourceFieldInt("fStatesCount", () -> statesCount),
                new SourceFieldInt("fStartState", () -> startState),
                new SourceFieldInt("dataSize", () -> dataSize),
                new SourceFieldInt("startSize", () -> startSize),
                new SourceFieldInt("offsetsSize", () -> offsetsSize),
                goTo,
                start,
                offsets,
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
        accepted.writeInt(i);
    }

    @Override
    public void setGoTo(int start, int input, int target) {
        compressor.set(start, input, String.valueOf(target));
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
        compressor.finish();
    }

    private void setTokenFile(String token) {
        String name = "Token" + StringUtils.capitalize(token);
        root.registerClazz("stupidcoder.compile.tokens." + name, "stupidcoder/template/Token.java");
        root.addClazzImport(name, "IToken");
        SourceCached srcName = new SourceCached("name");
        SourceCached srcId = new SourceCached("id");
        srcName.writeString(name);
        srcId.writeInt(loader == null ? 0 : loader.nameToTerminalId.get(token));
        root.registerClazzSrc(name, srcName, srcId);
    }

    @Override
    public void setData(int i, String val) {
        if (val != null) {
            goTo.set(i, val);
        }
    }

    @Override
    public void setStart(int i, int pos) {
        start.set(i, pos);
    }

    @Override
    public void setOffset(int i, int offset) {
        offsets.set(i, offset);
    }

    @Override
    public void setSize(int dataSize, int startSize, int offsetsSize) {
        this.dataSize = dataSize;
        this.startSize = startSize;
        this.offsetsSize = offsetsSize;
    }
}
