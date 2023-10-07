package stupidcoder.generate.sources.arr;

import stupidcoder.generate.sources.SourceCached;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SourceArrSetter extends SourceCached {
    public static final int EXTRACT_COMMON_DATA = 0x1;
    public static final int FOLD_OPTIMIZE = 0x2;
    protected int maxVarId = 0;
    protected List<String> varIdToData = new ArrayList<>();
    protected List<IArrDataSourceSetter> varIdToSetter = new ArrayList<>();
    protected Map<String, Integer> dataToVarId = new HashMap<>();
    protected List<Integer> repeatCount = new ArrayList<>();
    protected int flag = 0;
    protected IArrDataSourceSetter globalSetter = SourceCached::writeString;

    public SourceArrSetter(String srcId) {
        super(srcId);
    }

    public SourceArrSetter(String srcId, int flag) {
        super(srcId);
        this.flag = flag;
    }

    public final boolean isOpen(int f) {
        return (flag & f) != 0;
    }

    public void overrideGlobalSetter(IArrDataSourceSetter setter) {
        this.globalSetter = setter;
    }

    protected void writeDataExpr(int varId) {
        if (isOpen(EXTRACT_COMMON_DATA) && repeatCount.get(varId) > 1) {
            writeByte(0);
            writeString("e" + varId);
        } else {
            writeByte(1);
            varIdToSetter.get(varId).setSource(this, varIdToData.get(varId));
        }
    }

    public abstract void finish();

    @Override
    public final void lock() {
        this.finish();
        varIdToData = null;
        dataToVarId = null;
        repeatCount = null;
        super.lock();
    }


     static class Unit{
        final int k1, k2, hash;

        Unit(int k1, int k2) {
            this.k1 = k1;
            this.k2 = k2;
            this.hash = k1 + k2;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Unit u) {
                return u.k1 == k1 && u.k2 == k2;
            }
            return false;
        }

        @Override
        public String toString() {
            return k1 + "," + k2;
        }
    }
}
