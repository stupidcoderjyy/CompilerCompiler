package stupidcoder.generate;

import stupidcoder.Config;
import stupidcoder.generate.transform.*;
import stupidcoder.util.ASCII;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.BufferedInput;
import stupidcoder.util.input.CompileException;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generator {
    private final Map<String, Source> sources = new HashMap<>();
    private final Map<String, ITransform> transformers = new HashMap<>();
    private final String targetFile, scriptPath;
    private final BitClass clazzArgEnd = BitClass.of(',', '}', '\r');
    private final BitClass clazzArgBegin = BitClass.of('\"', '}', '\r');
    private final BitClass clazzQuoteEnd = BitClass.of('\"', '\r', ',', '}');
    private FileWriter writer;
    private BufferedInput input;
    private boolean locked = false;

    public Generator(String outPath, String scriptPath) {
        this.targetFile = Config.outputPath(outPath);
        this.scriptPath = Config.resourcePath(scriptPath);
        transformers.put("f", DefaultTransformers.FORMAT);
        transformers.put("format", DefaultTransformers.FORMAT);
        transformers.put("", DefaultTransformers.PLAIN);
        transformers.put("plain", DefaultTransformers.PLAIN);
        transformers.put("const", DefaultTransformers.CONST);
    }

    public void registerSrc(Source src) {
        if (locked) {
            throw new IllegalCallerException("generator locked");
        }
        sources.put(src.id, src);
    }

    public void registerTransform(String name, ITransform transform) {
        transformers.put(name, transform);
    }

    public void run() {
        sources.forEach((name, src) -> src.lock());
        this.locked = true;
        try (FileWriter w = new FileWriter(targetFile, StandardCharsets.UTF_8);
             BufferedInput i = BufferedInput.fromResource(scriptPath)) {
            writer = w;
            input = i;
            loadScript();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadScript() throws Exception {
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case '\n' -> {
                    writer.write(input.lexeme());
                    input.markLexemeStart();
                }
                case '$' -> {
                    Source src = parseSource();
                    Output out = parseOutput();
                    ITransform tr = parseTransform();
                    out.setSrc(src);
                    out.setTransform(tr);
                    out.output(writer);
                    tr.clear();
                    input.skipLine();
                    input.markLexemeStart();
                }
            }
        }
        writer.write(input.lexeme());
    }

    private Source parseSource() throws CompileException {
        input.markColumn();
        input.markLexemeStart();
        if ('$' != input.find('$', '\r', '{')) {
            throw rangeException("unclosed $");
        }
        input.retract();
        String srcName = input.lexeme();
        if (!sources.containsKey(srcName)) {
            throw rangeException("source not found: " + srcName);
        }
        return sources.get(srcName);
    }

    private Output parseOutput() throws CompileException {
        input.read();
        checkNext('{', "missing '{'");
        checkNext('\"', "missing '\"'");
        Output output = new Output();
        parseOutputType(output);
        return output;
    }

    private void parseOutputType(Output output) throws CompileException {
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case '-', '{' -> {
                    return;
                }
                case 'R', 'r' -> {
                    String str = readInt();
                    output.setRepeat(str.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(str));
                }
                case 'I', 'i' -> {
                    String str = readInt();
                    if (str.isEmpty()) {
                        throw pointException("missing indent count");
                    }
                    output.setIndent(Integer.parseInt(str));
                }
                case 'L', 'l' -> {
                    String str = readInt();
                    if (!str.isEmpty()) {
                        output.setLineBreak(Integer.parseInt(str));
                    }
                }
                default -> throw pointException("unknown symbol: \"" + (char)b + "\"");
            }
        }
    }

    private String readInt() {
        input.markLexemeStart();
        while (input.available()) {
            if (ASCII.isDigit(input.read())) {
                continue;
            }
            input.retract();
            break;
        }
        return input.lexeme();
    }

    private ITransform parseTransform() throws CompileException {
        input.markColumn();
        input.markLexemeStart();
        if (input.find(clazzQuoteEnd) != '\"') {
            throw rangeException("unclosed '\"'");
        }
        input.retract();
        String type = input.lexeme();
        ITransform t = transformers.get(type);
        if (t == null) {
            throw rangeException("transform not found: " + type);
        }
        input.read();
        initTransform(t);
        return t;
    }

    private void initTransform(ITransform t) throws CompileException {
        List<String> args = new ArrayList<>();
        LOOP:
        while (input.available()) {
            input.markColumn();
            int i = input.find(clazzArgEnd);
            switch (i) {
                case '}' -> {
                    break LOOP;
                }
                case ',' -> args.add(parseArg());
                default -> {
                    input.retract();
                    throw rangeException("unclosed arg");
                }
            }
        }
        t.init(args);
    }

    private String parseArg() throws CompileException {
        if (input.find(clazzArgBegin) != '\"') {
            throw pointException("missing \"");
        }
        input.markColumn();
        input.markLexemeStart();
        if (input.find(clazzQuoteEnd) != '\"') {
            throw rangeException("unclosed \"");
        }
        input.retract();
        return input.lexeme();
    }

    private CompileException pointException(String msg) {
        int p = input.posColumn() - 1;
        input.find('\r');
        return new CompileException(msg, input.posRow(), input.currentLine(), scriptPath).setPos(p);
    }

    private CompileException rangeException(String msg) {
        int mp = input.posColumnMark();
        int p = Math.max(mp, input.posColumn() - 1);
        input.find('\r');
        return new CompileException(msg, input.posRow(), input.currentLine(), scriptPath).setPos(mp, p);
    }

    private void checkNext(int expected, String msg) throws CompileException {
        if (input.read() != expected) {
            throw pointException(msg);
        }
    }
}
