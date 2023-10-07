package stupidcoder.core;

import org.apache.commons.lang3.StringUtils;
import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;
import stupidcoder.compile.syntax.ISyntaxAccess;
import stupidcoder.compile.syntax.ISyntaxAnalyzerSetter;
import stupidcoder.generate.generators.java.JProjectBuilder;
import stupidcoder.generate.sources.SourceCached;
import stupidcoder.generate.sources.SourceFieldInt;
import stupidcoder.generate.sources.arr.Source2DArrSetter;
import stupidcoder.generate.sources.arr.SourceArrSetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SrcGenSyntaxAnalyzer implements ISyntaxAnalyzerSetter {
    private int prodSize, statesCount, terminalCount, nonTerminalCount, remapSize;
    private final SourceCached srcRemap, srcProperty, srcSyntax;
    private final Source2DArrSetter goTo, actions;
    private final JProjectBuilder root;

    public SrcGenSyntaxAnalyzer(JProjectBuilder root) {
        this.root = root;
        this.srcRemap = new SourceCached("remap");
        this.srcProperty = new SourceCached("property");
        this.srcSyntax = new SourceCached("syntax");
        this.goTo = new Source2DArrSetter("goTo", SourceArrSetter.FOLD_OPTIMIZE);
        this.actions = new Source2DArrSetter("actions", SourceArrSetter.FOLD_OPTIMIZE);
        root.registerClazzSrc("SyntaxAnalyzer",
                new SourceFieldInt("prodSize", () -> prodSize),
                new SourceFieldInt("statesCount", () -> statesCount),
                new SourceFieldInt("terminalCount", () -> terminalCount),
                new SourceFieldInt("nonTerminalCount", () -> nonTerminalCount),
                new SourceFieldInt("remapSize", () -> remapSize),
                goTo,
                actions,
                srcRemap,
                srcProperty,
                srcSyntax);
    }

    @Override
    public void setActionShift(int from, int to, int inputTerminal) {
        actions.set(from, inputTerminal, "SHIFT | " + to);
    }

    @Override
    public void setActionReduce(int state, int forward, int productionId) {
        actions.set(state, forward, "REDUCE | " + productionId);
    }

    @Override
    public void setActionAccept(int state, int forward) {
        actions.set(state, forward, "ACCEPT");
    }

    @Override
    public void setGoto(int from, int to, int inputNonTerminal) {
        goTo.set(from, inputNonTerminal, to);
    }

    @Override
    public void setStatesCount(int count) {
        this.statesCount = count;
    }

    @Override
    public void setOthers(ISyntaxAccess access) {
        access.lexemeToSymbol().forEach((lexeme, symbol) -> {
            if (!symbol.isTerminal) {
                srcProperty.writeInt(symbol.id);
                String name = StringUtils.capitalize(lexeme);
                srcProperty.writeString(name);
                setPropertyFile(access, symbol, name);
            }
        });
        this.terminalCount = access.terminalSymbolsCount();
        this.nonTerminalCount = access.nonTerminalSymbolsCount();
        this.prodSize = access.syntax().size();
        int maxId = 0;
        for (var entry : access.terminalIdRemap().entrySet()) {
            maxId = Math.max(entry.getKey(), maxId);
            srcRemap.writeInt(entry.getKey(), entry.getValue());
        }
        this.remapSize = maxId + 1;
        setGrammar(access);
    }

    private void setGrammar(ISyntaxAccess access) {
        Map<String, Integer> lexemeToVarId = new HashMap<>();
        int varCount = 0;
        // symbols
        for (var entry : access.lexemeToSymbol().entrySet()) {
            Symbol s = entry.getValue();
            String l = entry.getKey();
            lexemeToVarId.put(l, varCount);
            srcSyntax.writeInt(0); //switch
            srcSyntax.writeInt(varCount);
            srcSyntax.writeString(l);
            srcSyntax.writeString(s.isTerminal ? "true" : "false");
            srcSyntax.writeInt(s.id);
            varCount++;
        }
        // productions
        List<Production> syntax = access.syntax();
        for (int i = 0; i < syntax.size(); i++) {
            Production p = syntax.get(i);
            srcSyntax.writeInt(1); //switch
            srcSyntax.writeInt(i, i, lexemeToVarId.get(p.head().toString()));
            srcSyntax.writeInt(p.body().size()); // repeat count
            for (Symbol s : p.body()) {
                srcSyntax.writeInt(lexemeToVarId.get(s.toString()));
            }
            srcSyntax.writeString(p.toString());
        }
    }

    private void setPropertyFile(ISyntaxAccess access, Symbol s, String name) {
        String clazzName = "Property" + name;
        String clazzPath = "stupidcoder.compile.properties." + clazzName;
        root.registerClazz(clazzPath, "stupidcoder/template/Property.java");
        SourceCached srcName = new SourceCached("name");
        srcName.writeString(clazzName);
        SourceCached srcReduceCall = new SourceCached("reduceCall");
        SourceCached srcReduceFunc = new SourceCached("reduceFunc");
        List<Production> ps = access.productionsWithHead(s);
        int size = ps.size();
        srcReduceFunc.writeInt(size); //repeat $r[reduceFunc]{
        if (size > 1) {
            srcReduceCall.writeInt(1); //switch
            srcReduceCall.writeInt(size); //repeat
            for (int i = 0 ; i < size ; i ++) {
                Production p = ps.get(i);
                srcReduceCall.writeInt(p.id(), i); //$f{"case %d -> reduce%d("}
                srcReduceFunc.writeString(p.toString());
                srcReduceFunc.writeInt(i); // $f{"private void reduce%d("}
                writeProduction(srcReduceCall, p);
                writeProduction(srcReduceFunc, p);
            }
        } else {
            Production p = ps.get(0);
            srcReduceCall.writeInt(0); //switch
            srcReduceFunc.writeString(p.toString());
            srcReduceFunc.writeInt(0); // $f{"private void reduce%d("}
            writeProduction(srcReduceCall, p);
            writeProduction(srcReduceFunc, p);
        }
        root.registerClazzSrc(clazzName,
                srcName,
                srcReduceCall,
                srcReduceFunc);
    }

    private void writeProduction(SourceCached src, Production p) {
        src.writeInt(p.body().size()); // repeat
        int i = 0;
        for (Symbol bs : p.body()) {
            if (bs.isTerminal) {
                src.writeInt(1); //switch
            } else {
                src.writeInt(0);
                src.writeString(StringUtils.capitalize(bs.toString()));
            }
            src.writeInt(i++);
        }
    }
}
