package stupidcoder.core;

import org.apache.commons.lang3.StringUtils;
import stupidcoder.compile.syntax.ISyntaxAnalyzerSetter;
import stupidcoder.compile.syntax.LRGroupBuilder;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.util.Config;
import stupidcoder.util.arrcompressor.ArrayCompressor;
import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.symbol.Symbol;
import stupidcoder.util.generate.project.java.IJavaProjectAdapter;
import stupidcoder.util.generate.project.java.JProjectBuilder;
import stupidcoder.util.generate.sources.SourceCached;
import stupidcoder.util.generate.sources.SourceFieldInt;
import stupidcoder.util.generate.sources.arr.CompressedArrSourceSetter;
import stupidcoder.util.generate.sources.arr.Source2DArrSetter;
import stupidcoder.util.generate.sources.arr.SourceArrSetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyntaxAnalyzerBuilder implements ISyntaxAnalyzerSetter, IJavaProjectAdapter {
    private int prodSize, statesCount, terminalCount, nonTerminalCount, remapSize;
    private int goToSize, goToStartSize, goToOffsetsSize, actionsSize, actionsStartSize, actionsOffsetsSize;
    private final SourceCached srcRemap, srcProperty, srcSyntax;
    private final Source2DArrSetter goTo, actions;
    private JProjectBuilder root;
    private ArrayCompressor goToCompressor, actionsCompressor;
    private final boolean compressUsed = Config.getBool(CompilerGenerator.USE_COMPRESSED_ARR);
    private final SyntaxLoader loader;

    public SyntaxAnalyzerBuilder(SyntaxLoader loader) {
        this.loader = loader;
        this.srcRemap = new SourceCached("remap");
        this.srcProperty = new SourceCached("property");
        this.srcSyntax = new SourceCached("syntax");
        this.goTo = new Source2DArrSetter("goTo", SourceArrSetter.FOLD_OPTIMIZE);
        this.actions = new Source2DArrSetter("actions", SourceArrSetter.FOLD_OPTIMIZE);
        if (compressUsed) {
            this.goToCompressor = new ArrayCompressor(new CompressedArrSourceSetter(goTo) {
                @Override
                public void setSize(int dataSize, int startSize, int offsetsSize) {
                    goToSize = dataSize;
                    goToStartSize = startSize;
                    goToOffsetsSize = offsetsSize;
                }
            });
            this.actionsCompressor = new ArrayCompressor(new CompressedArrSourceSetter(actions) {
                @Override
                public void setSize(int dataSize, int startSize, int offsetsSize) {
                    actionsSize = dataSize;
                    actionsStartSize = startSize;
                    actionsOffsetsSize = offsetsSize;
                }
            });
        }
    }

    @Override
    public void build(JProjectBuilder builder) {
        this.root = builder;
        LRGroupBuilder.build(loader, this);
        if (compressUsed) {
            root.addClazzInternalImport("SyntaxAnalyzer", "ArrayCompressor");
        }
        root.registerClazzSrc("SyntaxAnalyzer",
                new SourceFieldInt("prodSize", () -> prodSize),
                new SourceFieldInt("statesCount", () -> statesCount),
                new SourceFieldInt("terminalCount", () -> terminalCount),
                new SourceFieldInt("nonTerminalCount", () -> nonTerminalCount),
                new SourceFieldInt("remapSize", () -> remapSize),
                new SourceFieldInt("goToSize", () -> goToSize),
                new SourceFieldInt("goToStartSize", () -> goToStartSize),
                new SourceFieldInt("goToOffsetsSize", () -> goToOffsetsSize),
                new SourceFieldInt("actionsSize", () -> actionsSize),
                new SourceFieldInt("actionsStartSize", () -> actionsStartSize),
                new SourceFieldInt("actionsOffsetsSize", () -> actionsOffsetsSize),
                new SourceFieldInt("compressUsed", () -> compressUsed ? 0 : 1),
                goTo,
                actions,
                srcRemap,
                srcProperty,
                srcSyntax);
        root.addClazzPkgImport("SyntaxAnalyzer", "compile.properties");
    }

    @Override
    public void setActionShift(int from, int to, int inputTerminal) {
        if (compressUsed) {
            actionsCompressor.set(from, inputTerminal, "SHIFT | " + to);
        } else {
            actions.set(from, inputTerminal, "SHIFT | " + to);
        }
    }

    @Override
    public void setActionReduce(int state, int forward, int productionId) {
        if (compressUsed) {
            actionsCompressor.set(state, forward, "REDUCE | " + productionId);
        } else {
            actions.set(state, forward, "REDUCE | " + productionId);
        }
    }

    @Override
    public void setActionAccept(int state, int forward) {
        if (compressUsed) {
            actionsCompressor.set(state, forward, "ACCEPT");
        } else {
            actions.set(state, forward, "ACCEPT");
        }
    }

    @Override
    public void setGoto(int from, int to, int inputNonTerminal) {
        if (compressUsed) {
            goToCompressor.set(from, inputNonTerminal, String.valueOf(to));
        } else {
            goTo.set(from, inputNonTerminal, String.valueOf(to));
        }
    }

    @Override
    public void setStatesCount(int count) {
        this.statesCount = count;
    }

    @Override
    public void setOthers(SyntaxLoader loader) {
        loader.lexemeToSymbol.forEach((lexeme, symbol) -> {
            if (!symbol.isTerminal) {
                srcProperty.writeInt(symbol.id);
                String name = StringUtils.capitalize(lexeme);
                srcProperty.writeString(name);
                setPropertyFile(loader, symbol, name);
            }
        });
        this.terminalCount = loader.terminalCount;
        this.nonTerminalCount = loader.nonTerminalCount;
        this.prodSize = loader.productions.size();
        int maxId = 0;
        for (var entry : loader.terminalIdRemap.entrySet()) {
            maxId = Math.max(entry.getKey(), maxId);
            srcRemap.writeInt(entry.getKey(), entry.getValue());
        }
        this.remapSize = maxId + 1;
        setGrammar(loader);
        if (compressUsed) {
            goToCompressor.finish();
            actionsCompressor.finish();
        }
    }

    private void setGrammar(SyntaxLoader loader) {
        Map<String, Integer> lexemeToVarId = new HashMap<>();
        int varCount = 0;
        // symbols
        for (var entry : loader.lexemeToSymbol.entrySet()) {
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
        List<Production> syntax = loader.productions;
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

    private void setPropertyFile(SyntaxLoader access, Symbol s, String name) {
        String clazzName = "Property" + name;
        String clazzPath = "compile.properties." + clazzName;
        SourceCached srcName = new SourceCached("name");
        SourceCached srcReduceCall = new SourceCached("reduceCall");
        SourceCached srcReduceFunc = new SourceCached("reduceFunc");
        srcName.writeString(clazzName);
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
        root.registerClazz(clazzPath, "template/$Property.java");
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
