package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.SourceCached;

import java.util.Random;

public class TestSwitchOut {

    @Test
    public void testBasic() {
        SourceCached src = new SourceCached("src");
        for (int i = 0 ; i < 10 ; i ++) {
            src.writeInt(new Random().nextInt(2)); //switch
            src.writeInt(i);
        }
        Generator g = new Generator();
        g.registerSrc(src);
        g.loadScript("generate/out/switch/basic1.txt", "switch_basic.txt");
    }
}
