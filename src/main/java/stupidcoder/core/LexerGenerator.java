package stupidcoder.core;

import stupidcoder.compile.lex.IDfaSetter;
import stupidcoder.generate.generators.java.JProjectBuilder;
import stupidcoder.generate.sources.SourceCached;
import stupidcoder.generate.sources.SourceFieldInt;
import stupidcoder.generate.sources.arr.HighFreqPoint;
import stupidcoder.generate.sources.arr.SourceArrSetterIII;
import stupidcoder.generate.sources.arr.SourceArrSetterIS;

public class LexerGenerator implements IDfaSetter {
    private int statesCount, startState;
    private final SourceArrSetterIII goToSetter;
    private final SourceArrSetterIS opSetter;
    private final SourceCached accepted;

    public LexerGenerator(JProjectBuilder root) {
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

    @Override
    public void setAccepted(int i, String token) {
        token = Character.toUpperCase(token.charAt(0)) + token.substring(1);
        opSetter.set(i, String.format("(l, i) -> new Token%s().fromLexeme()", token));
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
}
