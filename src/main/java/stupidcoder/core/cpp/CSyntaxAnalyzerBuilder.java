package stupidcoder.core.cpp;

import org.apache.commons.lang3.StringUtils;
import stupidcoder.syntax.ISyntaxAnalyzerSetter;
import stupidcoder.syntax.LRGroupBuilder;
import stupidcoder.syntax.SyntaxLoader;
import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.symbol.Symbol;
import stupidcoder.util.generate.Generator;
import stupidcoder.util.generate.project.cpp.CProjectBuilder;
import stupidcoder.util.generate.project.cpp.ICppProjectAdapter;
import stupidcoder.util.generate.sources.SourceCached;
import stupidcoder.util.generate.sources.SourceFieldInt;
import stupidcoder.util.generate.sources.arr.Source2DArrSetter;
import stupidcoder.util.generate.sources.arr.SourceArrSetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSyntaxAnalyzerBuilder implements ISyntaxAnalyzerSetter, ICppProjectAdapter {
    private final SyntaxLoader loader;
    private final SourceCached srcPropertyClasses = new SourceCached("propertyClasses");
    private int propertyCount;
    private int prodCount;
    private final SourceFieldInt srcPropertyCount = new SourceFieldInt("propertyCount", () -> propertyCount);
    private final SourceFieldInt srcPropertyCountCpp = new SourceFieldInt("propertyCountCpp", () -> propertyCount);
    private final SourceFieldInt srcProdCount = new SourceFieldInt("prodCount", () -> prodCount);
    private final SourceCached srcPropertyClassDef = new SourceCached("propertyClassDef");
    private final SourceCached srcData = new SourceCached("data");
    private final SourceCached srcRemap = new SourceCached("remap");
    private final SourceCached srcProperty = new SourceCached("property");
    private final SourceCached srcSyntax = new SourceCached("syntax");
    private final SourceCached srcPropertyClassImpl = new SourceCached("propertyClassImpl");
    private final SourceCached srcPropertyReduceFunc = new SourceCached("propertyReduceFunc");
    private final Source2DArrSetter goTo = new Source2DArrSetter("goTo", SourceArrSetter.FOLD_OPTIMIZE);
    private final Source2DArrSetter actions = new Source2DArrSetter("actions", SourceArrSetter.FOLD_OPTIMIZE);
    private int statesCount;

    public CSyntaxAnalyzerBuilder(SyntaxLoader loader) {
        this.loader = loader;
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
        goTo.set(from, inputNonTerminal, String.valueOf(to));
    }

    @Override
    public void setStatesCount(int count) {
        statesCount = count;
    }

    @Override
    public void setOthers(SyntaxLoader loader) {
        loader.lexemeToSymbol.forEach((lexeme, symbol) -> {
            if (!symbol.isTerminal) {
                String name = StringUtils.capitalize(lexeme);
                srcProperty.writeInt(symbol.id);
                srcProperty.writeString(name);
                writeProperty(loader, symbol, name);
            }
        });
        prodCount = loader.productionCount;
        int maxId = 0;
        for (var entry : loader.terminalIdRemap.entrySet()) {
            maxId = Math.max(entry.getKey(), maxId);
            srcRemap.writeInt(entry.getKey(), entry.getValue());
        }
        writeGrammar(loader);
        //prodSize, remapSize, nonTerminalCount, terminalCount, statesCount
        srcData.writeInt(loader.productions.size(),
                maxId + 1,
                loader.nonTerminalCount,
                loader.terminalCount,
                statesCount);
    }

    private void writeGrammar(SyntaxLoader loader) {
        Map<String, Integer> lexemeToVarId = new HashMap<>();
        int varCount = 0;
        // symbols
        for (var entry : loader.lexemeToSymbol.entrySet()) {
            Symbol s = entry.getValue();
            String l = entry.getKey();
            lexemeToVarId.put(l, varCount);
            srcSyntax.writeInt(0); //switch
            srcSyntax.writeString(s.isTerminal ? "true" : "false");
            srcSyntax.writeInt(s.id);
            varCount++;
        }
        // productions
        List<Production> syntax = loader.productions;
        for (int i = 0; i < syntax.size(); i++) {
            Production p = syntax.get(i);
            srcSyntax.writeInt(1); //switch
            srcSyntax.writeInt(i, i, lexemeToVarId.get(p.head().toString()), p.body().size());
            srcSyntax.writeInt(p.body().size()); // repeat count
            for (Symbol s : p.body()) {
                srcSyntax.writeInt(lexemeToVarId.get(s.toString()));
            }
            srcSyntax.writeString(p.toString());
        }
    }

    private void writeProperty(SyntaxLoader access, Symbol s, String name) {
        propertyCount++;
        srcPropertyClasses.writeString(name);

        srcPropertyClassDef.writeString(name);
        srcPropertyReduceFunc.writeString(name);
        List<Production> ps = access.productionsWithHead(s);
        int size = ps.size();
        if (size > 1) {
            srcPropertyReduceFunc.writeInt(1); //switch
            srcPropertyReduceFunc.writeInt(size); //repeat
            for (int i = 0 ; i < size ; i ++) {
                Production p = ps.get(i);
                srcPropertyReduceFunc.writeInt(p.id(), i); //$f{"case %d: reduce%d("}
                writeProduction(srcPropertyReduceFunc, p);
            }
        } else {
            Production p = ps.get(0);
            srcPropertyReduceFunc.writeInt(0); //switch
            writeProduction(srcPropertyReduceFunc, p);
        }
        srcPropertyClassDef.writeInt(size);
        for (int i = 0 ; i < size ; i ++) {
            Production p = ps.get(i);
            srcPropertyClassDef.writeInt(i);    //$f{"void reduce%d("} +
            writeProduction(srcPropertyClassDef, p);
            srcPropertyClassDef.writeString(p.toString());
            srcPropertyClassImpl.writeString(p.toString()); // $f{"//%s"}
            srcPropertyClassImpl.writeString(name); //$f{"void Property%s::reduce%d("}
            srcPropertyClassImpl.writeInt(i);
            writeProduction(srcPropertyClassImpl, p);
        }
    }

    private void writeProduction(SourceCached src, Production p) {
        src.writeInt(p.body().size()); // repeat
        int i = 0;
        for (Symbol bs : p.body()) {
            src.writeString(bs.isTerminal ?
                    "Terminal" :
                    StringUtils.capitalize(bs.toString()));
            src.writeInt(i++);
        }
    }

    @Override
    public void build(CProjectBuilder builder) {
        LRGroupBuilder.build(loader, this);

        Generator headerFile = new Generator();
        headerFile.registerSrc(srcPropertyClasses);
        headerFile.registerSrc(srcPropertyClassDef);
        headerFile.registerSrc(srcPropertyCount);

        Generator sourceFile = new Generator();
        sourceFile.registerSrc(srcData);
        sourceFile.registerSrc(actions);
        sourceFile.registerSrc(goTo);
        sourceFile.registerSrc(srcRemap);
        sourceFile.registerSrc(srcProperty);
        sourceFile.registerSrc(srcSyntax);
        sourceFile.registerSrc(srcPropertyClassImpl);
        sourceFile.registerSrc(srcProdCount);
        sourceFile.registerSrc(srcPropertyReduceFunc);
        sourceFile.registerSrc(srcPropertyCountCpp);

        builder.include("SyntaxAnalyzer.h", headerFile);
        builder.include("SyntaxAnalyzer.cpp", sourceFile);
    }
}
