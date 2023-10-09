package stupidcoder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final int STRING_VALUE = 0x1000;
    private static final int BOOL_VALUE = 0x2000;
    private static final int INT_VALUE = 0x3000;
    private static final int AUTO_CREATE_PATH = STRING_VALUE | 0x100;

    public static final int OUTPUT_DIR = AUTO_CREATE_PATH | 1;
    public static final int TEMP_OUT = AUTO_CREATE_PATH | 2;
    public static final int SRC_DIR = STRING_VALUE | 1;
    public static final int SHOW_ACTION_CONFLICT = BOOL_VALUE | 1;
    public static final int COMPRESSED_ARR = BOOL_VALUE | 2;
    public static final int KEY_WORD = BOOL_VALUE | 3;
    public static final int SYNTAX_DEBUG_INFO = BOOL_VALUE | 4;

    private static final Config INSTANCE = new Config();
    private final Map<Integer, Object> configs = new HashMap<>();

    private Config() {
        configs.put(OUTPUT_DIR, "build/out");
        configs.put(SRC_DIR, "");
        configs.put(TEMP_OUT, "build/out");
        configs.put(SHOW_ACTION_CONFLICT, false);
        configs.put(COMPRESSED_ARR, false);
        configs.put(KEY_WORD, false);
        configs.put(SYNTAX_DEBUG_INFO, false);
        new File("build/out").mkdirs();
    }

    public static void set(int type, Object data) {
        INSTANCE.set0(type, data);
    }

    public static void register(int type, Object data) {
        INSTANCE.register0(type, data);
    }

    public static String getString(int type) {
        INSTANCE.check(type, STRING_VALUE);
        return (String) INSTANCE.configs.get(type);
    }

    public static boolean getBool(int type) {
        INSTANCE.check(type, BOOL_VALUE);
        return (boolean) INSTANCE.configs.get(type);
    }

    private void check(int type, int requiredType) {
        if ((type & requiredType) == 0) {
            throw new IllegalArgumentException("mismatched type");
        }
        if (!configs.containsKey(type)) {
            throw new IllegalArgumentException("type not found: 0x" + Integer.toHexString(type));
        }
    }

    private void set0(int type, Object data) {
        if (!configs.containsKey(type)) {
            throw new IllegalArgumentException("invalid type: 0x" + Integer.toHexString(type));
        }
        if ((type & AUTO_CREATE_PATH) != 0) {
            new File((String) data).mkdirs();
        }
        configs.put(type, data);
    }

    private void register0(int type, Object data) {
        if (checkType(type, data)) {
            if ((type & AUTO_CREATE_PATH) != 0) {
                new File((String) data).mkdirs();
            }
            configs.put(type, data);
        }
    }

    private boolean checkType(int type, Object data) {
        return switch (type & 0xF000) {
            case STRING_VALUE -> data instanceof String;
            case BOOL_VALUE -> data instanceof Boolean;
            case INT_VALUE -> data instanceof Integer;
            default -> throw new IllegalArgumentException(
                    "missing or invalid type flag: 0x" + Integer.toHexString(type));
        };
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
