package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.SourceCached;

public class TestSwitchOut {

    @Test
    public void testBasic() {
        SourceCached src = new SourceCached("src");
        src.writeInt(1); //item id
        src.writeInt(12); //data
        Generator g = new Generator();
        g.registerSrc(src);
        g.loadScript("/generate/out/switch/basic.txt", "switch_basic.txt");
    }
}
