package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.SourceCached;

public class TestRepeatOut {

    @Test
    public void testBasic() {
        Generator g = new Generator();
        SourceCached src = new SourceCached("src");
        src.writeInt(3); // 重复次数
        for (int i = 0 ; i < 3 ; i ++) {
            src.writeInt(i); //前缀
            src.writeInt(i);
            src.writeInt(i); //后缀
        }
        g.registerSrc(src);
        g.loadScript("/generate/out/repeat/basic.txt", "repeat_basic.txt");
    }

    @Test
    public void testErr() {
        Generator g = new Generator();
        for (int i = 1 ; i <= 3 ; i ++) {
            g.loadScript(String.format("/generate/out/repeat/err%d.txt", i), "err.txt");
        }
    }
}
