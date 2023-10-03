package stupidcoder.generate.sources.arr;

import stupidcoder.generate.sources.SourceCached;
import stupidcoder.util.OpenRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceArrSetterIII extends SourceCached {
    private final HighFreqPoint point;
    private Map<Unit, OpenRange> setters = new HashMap<>();
    private Map<Unit, List<Unit>> repeatedFor = new HashMap<>();

    public SourceArrSetterIII(String name, HighFreqPoint highFreqPoint) {
        super(name);
        this.point = highFreqPoint;
    }

    public void set(int p1, int p2, int data) {
        ensureState(false);
        switch (point) {
            case ARG_1 -> set0(p2, data, p1);
            case ARG_2 -> set0(p1, data, p2);
            case DATA -> set0(p1, p2, data);
        }
    }

    @Override
    public void lock() {
        finish();
        super.lock();
    }

    public void finish() {
        setters.forEach((key, range) -> {
            int key1 = key.k1;
            int key2 = key.k2;
            range.forEach((l, r) -> {
                if (r - l > 2) {
                    Unit k = new Unit(l + 1, r - 1);
                    repeatedFor.computeIfAbsent(k, k1 -> new ArrayList<>()).add(key);
                } else {
                    writeInt(0); //switch
                    point.singleSetter.write(this, key1, key2, l + 1);
                }
            });
        });
        repeatedFor.forEach((key, list) -> {
            writeInt(1); //switch
            writeInt(key.k1);
            writeInt(key.k2);
            writeInt(list.size()); //repeat
            for (Unit k : list) {
                writeInt(k.k1);
                writeInt(k.k2);
            }
        });
        setters = null;
        repeatedFor = null;
    }

    private void set0(int key1, int key2, int hf) {
        Unit key = new Unit(key1, key2);
        OpenRange range = setters.get(key);
        if (range == null) {
            range = OpenRange.empty();
            setters.put(key, range);
        }
        range.union(hf);
    }

    private static class Unit{
        private final int k1, k2, hash;

        private Unit(int k1, int k2) {
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
