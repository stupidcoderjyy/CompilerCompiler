package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.SourceCached;

public class TestRepeatOut {

    @Test
    public void testBasic() {
        Generator g = new Generator();
        SourceCached src1 = new SourceCached("src1");
        SourceCached src2 = new SourceCached("src2");
        src1.writeInt(3); // 重复次数
        for (int i = 0 ; i < 3 ; i ++) {
            src1.writeInt(i); //前缀
            src1.writeInt(i);
            src1.writeInt(i); //后缀
        }
        src2.writeInt(1); // 重复次数
        src2.writeInt(0, 0, 0); // 重复次数
        g.registerSrc(src1);
        g.registerSrc(src2);
        g.loadScript("generate/out/repeat/basic.txt", "repeat_basic.txt");
    }

    @Test
    public void testErr() {
        Generator g = new Generator();
        for (int i = 1 ; i <= 3 ; i ++) {
            g.loadScript(String.format("generate/out/repeat/err%d.txt", i), "err.txt");
        }
    }
}
