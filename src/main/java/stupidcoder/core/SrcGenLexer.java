package stupidcoder.core;

import org.apache.commons.lang3.StringUtils;
import stupidcoder.compile.lex.IDfaSetter;
import stupidcoder.generate.generators.java.JProjectBuilder;
import stupidcoder.generate.sources.SourceCached;
import stupidcoder.generate.sources.SourceFieldInt;
import stupidcoder.generate.sources.arr.HighFreqPoint;
import stupidcoder.generate.sources.arr.SourceArrSetterIII;
import stupidcoder.generate.sources.arr.SourceArrSetterIS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SrcGenLexer implements IDfaSetter {
    private int statesCount, startState;
    private final SourceArrSetterIII goToSetter;
    private final SourceArrSetterIS opSetter;
    private final SourceCached accepted;
    private final JProjectBuilder root;
    private final ScriptLoader loader;

    public SrcGenLexer(ScriptLoader loader, JProjectBuilder root) {
        this.loader = loader;
        this.root = root;
        this.goToSetter = new SourceArrSetterIII("goTo", HighFreqPoint.ARG_2);
        this.opSetter = new SourceArrSetterIS("op");
        this.accepted = new SourceCached("accepted");
        root.registerClazzSrc("Lexer",
                new SourceFieldInt("fStatesCount", () -> statesCount),
                new SourceFieldInt("fStartState", () -> startState),
                goToSetter,
                opSetter,
                accepted);
    }

    public SrcGenLexer(JProjectBuilder root) {
        this(null, root);
    }

    @Override
    public void setAccepted(int i, String token) {
        String tokenName = StringUtils.capitalize(token);
        opSetter.set(i, tokenName);
        accepted.writeInt(i);
    }

    @Override
    public void setGoTo(int start, int input, int target) {
        goToSetter.set(start, input, target);
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
}
