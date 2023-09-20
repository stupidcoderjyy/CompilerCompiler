package generate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import stupidcoder.generate.source.CacheSource;
import stupidcoder.util.ReflectionUtil;

public class TestCacheSource {

    @Test
    public void testWriteInt() {
        CacheSource s = new CacheSource("a_b");
        s.writeInt(0xEEFFEEFF);
        byte[] data = ReflectionUtil.getObjectField(s, "data");
        int count = ReflectionUtil.getObjectField(s, "count");
        Assertions.assertEquals(0xEE, data[0] & 0xFF);
        Assertions.assertEquals(0xFF, data[1] & 0xFF);
        Assertions.assertEquals(0xEE, data[2] & 0xFF);
        Assertions.assertEquals(0xFF, data[3] & 0xFF);
        Assertions.assertEquals(4, count);
        s.close();
    }

    @Test
    public void testWriteString() {
        CacheSource s = new CacheSource("a_b");
        s.writeString("0123456789");
        byte[] data = ReflectionUtil.getObjectField(s, "data");
        int count = ReflectionUtil.getObjectField(s, "count");
        Assertions.assertEquals(10, data[3]);
        Assertions.assertEquals('1', data[5]);
        Assertions.assertEquals(14, count);
        s.close();
    }

    @Test
    public void testCreateCache() {
        CacheSource s = new CacheSource("a_b");
        for (int i = 0 ; i < 512 ; i ++) {
            s.writeByte('1');
        }
        s.writeByte('2');
        s.close();
    }
}
