package stupidcoder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final int FLAG_OUT_PATH = 0x10;
    public static final int OUTPUT_DIR = FLAG_OUT_PATH | 1;
    public static final int SRC_DIR = 1;
    public static final int TEMP_OUT = FLAG_OUT_PATH | 2;

    private static final Config INSTANCE = new Config();
    private final Map<Integer, String> configs = new HashMap<>();

    private Config() {
        configs.put(OUTPUT_DIR, "build/out");
        configs.put(SRC_DIR, "");
        configs.put(TEMP_OUT, "build/out");
        new File("build/out").mkdirs();
    }

    public static void set(int type, String data) {
        if (!INSTANCE.configs.containsKey(type)) {
            throw new IllegalArgumentException("unknown type: " + type);
        }
        if ((type & FLAG_OUT_PATH) != 0) {
            new File(data).mkdirs();
        }
        INSTANCE.configs.put(type, data);
    }

    public static String outputPath(String child) {
        return INSTANCE.configs.get(OUTPUT_DIR) + "/" + child;
    }

    public static String tempOutPath(String child) {
        return INSTANCE.configs.get(TEMP_OUT) + "/" + child;
    }

    public static String resourcePath(String child) {
        return INSTANCE.configs.get(SRC_DIR) + "/" + child;
    }
}
