package stupidcoder.generate.sources;

import stupidcoder.Config;
import stupidcoder.generate.Source;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SourceCached extends Source {
    private static int srcCount = 0;
    private static final String CACHE_DIR;
    private static final int DATA_SIZE_THRESHOLD = 128;
    private static final Pattern NAME_PATTERN = Pattern.compile("[_a-z][_a-zA-Z0-9]*");
    private byte[] data;
    private int count;
    private BufferedOutputStream cacheOut;
    private BufferedInputStream cacheIn;
    private boolean useCache;
    protected boolean locked;
    private final int id;

    static {
        CACHE_DIR = Config.tempOutPath("source-caches");
        new File(CACHE_DIR).mkdirs();
    }

    public SourceCached(String name) {
        super(name);
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("source name \"" + name + "\" doesn't match [_a-z][_a-zA-Z0-9]*");
        }
        this.data = new byte[DATA_SIZE_THRESHOLD];
        this.useCache = false;
        this.count = 0;
        this.id = srcCount++;
        locked = false;
    }

    public void writeInt(int i) {
        try {
            ensureState(false);
            byte[] bs = new byte[]{
                    (byte) (i >> 24),
                    (byte) (i >> 16),
                    (byte) (i >> 8),
                    (byte) i
            };
            if (useCache || tooLarge(4)) {
                for (byte b : bs) {
                    cacheOut.write(b);
                }
            } else {
                int c = 0;
                while (c < 4) {
                    data[count++] = bs[c++];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeInt(int ... ints) {
        for (int anInt : ints) {
            writeInt(anInt);
        }
    }

    public void writeString(String str) {
        try {
            ensureState(false);
            byte[] temp = str.getBytes(StandardCharsets.UTF_8);
            writeInt(temp.length);
            if (useCache || tooLarge(temp.length)) {
                cacheOut.write(temp, 0, temp.length);
            } else {
                System.arraycopy(temp, 0, data, count, temp.length);
                count += temp.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeByte(int b) {
        try {
            ensureState(false);
            if (useCache || tooLarge(1)) {
                cacheOut.write(b);
            } else {
                data[count] =(byte) b;
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean tooLarge(int required) {
        if (count + required > DATA_SIZE_THRESHOLD) {
            prepareCache();
            return true;
        }
        return false;
    }

    private void prepareCache() {
        try {
            cacheOut = new BufferedOutputStream(new FileOutputStream(getCacheFileName()));
            cacheOut.write(data, 0, count);
            useCache = true;
            data = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lock() {
        try {
            if (locked) {
                return;
            }
            if (useCache) {
                cacheOut.close();
            }
            locked = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int read(byte[] arr, int offset, int len) {
        try {
            used = true;
            ensureState(true);
            if (useCache) {
                if (cacheIn == null) {
                    cacheIn = new BufferedInputStream(new FileInputStream(getCacheFileName()));
                }
                return cacheIn.read(arr, offset, len);
            }
            int actualLen = Math.min(len, count);
            System.arraycopy(data, count - actualLen, arr, offset, actualLen);
            count -= actualLen;
            return actualLen;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void close() {
        try {
            data = null;
            if (cacheIn != null) {
                cacheIn.close();
                cacheIn = null;
            }
            if (cacheOut != null) {
                cacheOut.close();
                cacheOut = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCacheFileName() {
        return CACHE_DIR + "/" + name + "$" + id + ".cache";
    }

    protected void ensureState(boolean locked) {
        if (this.locked != locked) {
            if (locked) {
                throw new IllegalStateException("source still open");
            } else {
                throw new IllegalCallerException("source locked");
            }
        }
    }
}
