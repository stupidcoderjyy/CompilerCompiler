package stupidcoder.generate;

import stupidcoder.Config;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompilerInput;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Generator {
    private static final Map<String, Transform> DEFAULT_TRANSFORMS = new HashMap<>();
    final WriteUnitParser parser;
    final Map<String, Source> sources;
    final Map<String, Transform> transformers;
    protected final String outFile, scriptPath;
    protected final Generator parent;
    private FileWriter writer;
    private CompilerInput input;
    protected boolean locked = false;
    
    static {
        DEFAULT_TRANSFORMS.put("f", DefaultTransforms.FORMAT);
        DEFAULT_TRANSFORMS.put("format", DefaultTransforms.FORMAT);
        DEFAULT_TRANSFORMS.put("", DefaultTransforms.PLAIN);
        DEFAULT_TRANSFORMS.put("plain", DefaultTransforms.PLAIN);
        DEFAULT_TRANSFORMS.put("const", DefaultTransforms.CONST);
        DEFAULT_TRANSFORMS.put("c", DefaultTransforms.COMPLEX);
        DEFAULT_TRANSFORMS.put("complex", DefaultTransforms.COMPLEX);
    }

    public Generator(String outPath, String scriptPath) {
        this(null, outPath, scriptPath);
    }

    public Generator(Generator parent,String outPath, String scriptPath) {
        this.outFile = Config.outputPath(outPath);
        this.scriptPath = Config.resourcePath(scriptPath);
        this.parent = parent;
        this.sources = new HashMap<>();
        this.transformers = new HashMap<>();
        this.parser = new WriteUnitParser(this);
    }

    public void registerSrc(Source src) {
        ensureUnlocked();
        sources.put(src.id, src);
    }

    public void registerTransform(String name, Transform transform) {
        ensureUnlocked();
        transformers.put(name, transform);
    }

    protected void ensureUnlocked() {
        if (locked) {
            throw new IllegalStateException("generator locked: " + this);
        }
    }

    public void run() {
        sources.forEach((name, src) -> src.lock());
        this.locked = true;
        try (FileWriter w = new FileWriter(outFile, StandardCharsets.UTF_8);
             CompilerInput i = CompilerInput.fromResource(scriptPath)) {
            writer = w;
            input = i;
            loadScript();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadScript() throws Exception {
        BitClass clazz = BitClass.of('\r', '$');
        while (true) {
            switch (input.approach(clazz)) {
                case '$' -> {
                    input.removeMark();
                    parser.parse(input).output(writer);
                    input.skipLine();
                    input.mark();
                }
                case -1 -> {
                    input.mark();
                    writer.write(input.capture());
                    return;
                }
                default -> {
                    input.read();
                    input.mark();
                    writer.write(input.capture());
                    input.mark();
                }
            }
        }
    }

    public Source getSource(String name) {
        if (name.isEmpty()) {
            return Source.EMPTY;
        }
        Source res;
        Generator g = this;
        while (g != null) {
            res = g.sources.get(name);
            if (res != null) {
                return res;
            }
            g = g.parent;
        }
        return null;
    }

    public Transform getTransform(String name) {
        Transform res = DEFAULT_TRANSFORMS.get(name);
        if (res != null) {
            return res;
        }
        Generator g = this;
        while (g != null) {
            res = g.transformers.get(name);
            if (res != null) {
                return res;
            }
            g = g.parent;
        }
        return null;
    }
}
