package stupidcoder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static Config INSTANCE;
    private final Map<String, String> configs = new HashMap<>();
    private final String outputRoot, resourceRoot;

    static {
        init("build/out", "");
    }

    public static void init(String outputRoot, String resourceRoot) {
        new File(outputRoot).mkdirs();
        INSTANCE = new Config(outputRoot, resourceRoot);
    }

    private Config(String outputRoot, String resourceRoot) {
        this.outputRoot = outputRoot;
        this.resourceRoot = resourceRoot;
    }

    public static String outputRoot() {
        return INSTANCE.outputRoot;
    }

    public static String outputPath(String child) {
        return INSTANCE.outputRoot + "/" + child;
    }

    public static String resourceRoot() {
        return INSTANCE.resourceRoot;
    }

    public static String resourcePath(String child) {
        return INSTANCE.resourceRoot + "/" + child;
    }
}
