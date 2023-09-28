package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.SourceCached;

public class TestFormatOut {

    @Test
    public void testBasic() {
        SourceCached src1 = new SourceCached("src1");
        src1.writeInt(12);
        src1.writeByte('a');
        src1.writeString("str");
        SourceCached src2 = new SourceCached("src2");
        src2.writeInt(2543);
        src2.writeInt(3211);
        Generator g = new Generator();
        g.registerSrc(src1);
        g.registerSrc(src2);
        g.loadScript("generate/out/format/basic.txt", "format_basic.txt");
    }

    @Test
    public void testErr() {
        Generator g = new Generator();
        for (int i = 1 ; i <= 5 ; i ++) {
            g.loadScript(String.format("generate/out/format/err%d.txt", i), "err.txt");
        }
    }
}
