package stupidcoder.generate;

import stupidcoder.Config;
import stupidcoder.generate.parse.InternalParsers;
import stupidcoder.generate.parse.Parser;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompilerInput;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Generator {
    protected static final Map<String, Parser> DEFAULT_UNIT_PARSERS = new HashMap<>();
    protected final Map<String, Source> sources = new HashMap<>();
    protected final Map<String, Parser> customUnitParsers = new HashMap<>();
    protected final Generator parent;

    static {
        DEFAULT_UNIT_PARSERS.put("f", InternalParsers.UNIT_FORMAT);
        DEFAULT_UNIT_PARSERS.put("format", InternalParsers.UNIT_FORMAT);
        DEFAULT_UNIT_PARSERS.put("c", InternalParsers.UNIT_COMPLEX);
        DEFAULT_UNIT_PARSERS.put("complex", InternalParsers.UNIT_COMPLEX);
        DEFAULT_UNIT_PARSERS.put("const", InternalParsers.UNIT_CONST);
        DEFAULT_UNIT_PARSERS.put("r", InternalParsers.UNIT_REPEAT);
        DEFAULT_UNIT_PARSERS.put("repeat", InternalParsers.UNIT_REPEAT);
        DEFAULT_UNIT_PARSERS.put("s", InternalParsers.UNIT_SWITCH);
        DEFAULT_UNIT_PARSERS.put("switch", InternalParsers.UNIT_SWITCH);
    }

    public Generator(Generator parent) {
        this.parent = parent;
    }

    public Generator() {
        this(null);
    }

    public void registerParser(String name, Parser parser) {
        customUnitParsers.put(name, parser);
    }

    public void registerSrc(Source src) {
        sources.put(src.name, src);
    }

    public void loadScript(String scriptFile, String targetFile) {
        sources.forEach((name, src) -> src.lock());
        try (CompilerInput input = CompilerInput.fromResource(Config.resourcePath(scriptFile));
             FileWriter writer = new FileWriter(Config.outputPath(targetFile), StandardCharsets.UTF_8)){
            loadScript(input, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sources.forEach((name, src) -> src.destroy());
    }

    public void loadScript(CompilerInput input, FileWriter writer) throws Exception{
        BitClass sign = BitClass.of('$', '\r');
        while (true) {
            input.mark();
            switch (input.approach(sign)) {
                case '$' -> {
                    input.removeMark();
                    InternalParsers.UNIT
                            .parse(this, input, null)
                            .writeAll(writer, null);
                    input.skipLine();
                }
                case '\r' -> {
                    input.mark();
                    writer.write(input.capture());
                    writer.write("\r\n");
                    input.skipLine();
                }
                default -> {
                    input.mark();
                    writer.write(input.capture());
                    return;
                }
            }
        }
    }

    public Source getSrc(String name) {
        Generator g = this;
        Source src;
        while (g != null) {
            src = g.sources.get(name);
            if (src != null) {
                return src;
            }
            g = g.parent;
        }
        return null;
    }

    public Parser getUnitParser(String name) {
        Parser p = DEFAULT_UNIT_PARSERS.get(name);
        if (p != null) {
            return p;
        }
        Generator g = this;
        while (g != null) {
            p = g.customUnitParsers.get(name);
            if (p != null) {
                return p;
            }
            g = g.parent;
        }
        return null;
    }
}
