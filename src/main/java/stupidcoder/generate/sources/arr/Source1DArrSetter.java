package stupidcoder.generate.sources.arr;

import stupidcoder.util.OpenRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Source1DArrSetter extends SourceArrSetter {
    // fold
    private Map<Integer, OpenRange> varIdToPos = new HashMap<>();
    // fold disabled
    private Map<Integer, Integer> posToVarId = new HashMap<>();

    private Map<Unit, List<Integer>> repeatedFor = new HashMap<>();

    public Source1DArrSetter(String srcId) {
        super(srcId);
    }

    public Source1DArrSetter(String srcId, int flag) {
        super(srcId, flag);
    }

    public void set(int i, String data, IArrDataSourceSetter setter) {
        int varId = dataToVarId.compute(data, (k, v) -> {
            if (v == null) {
                if (isOpen(EXTRACT_COMMON_DATA)) {
                    repeatCount.add(1);
                }
                varIdToData.add(data);
                varIdToSetter.add(setter);
                return maxVarId++;
            }
            if (isOpen(EXTRACT_COMMON_DATA)) {
                repeatCount.set(v, repeatCount.get(v) + 1);
            }
            varIdToSetter.set(v, setter);
            return v;
        });
        if (isOpen(FOLD_OPTIMIZE)) {
            varIdToPos.compute(varId, (k, v) -> v == null ? OpenRange.empty() : v).union(i);
        } else {
            posToVarId.put(i, varId);
        }
    }

    public void set(int i, String data) {
        set(i, data, globalSetter);
    }

    public void set(int i, int data, IArrDataSourceSetter setter) {
        set(i, Integer.toString(data), setter);
    }

    public void set(int i, int data) {
        set(i, data, globalSetter);
    }

    @Override
    public void finish() {
        if (posToVarId == null) {
            return;
        }
        if (isOpen(EXTRACT_COMMON_DATA)) {
            for (int varId = 0; varId < repeatCount.size(); varId++) {
                if (repeatCount.get(varId) == 1) {
                    continue;
                }
                writeByte(0); //switch 变量定义
                writeInt(varId);
                globalSetter.setSource(this, varIdToData.get(varId));
            }
        }
        if (isOpen(FOLD_OPTIMIZE)) {
            varIdToPos.forEach((varId, ranges) -> ranges.forEach((l, r) -> {
                switch (r - l) {
                    case 3 -> {
                        writeNormal(l + 1, varId);
                        writeNormal(l + 2, varId);
                    }
                    case 2 -> writeNormal(l + 1, varId);
                    default -> repeatedFor.compute(new Unit(l, r), (uFor, list) -> {
                        list = list == null ? new ArrayList<>() : list;
                        list.add(varId);
                        return list;
                    });
                }
            }));
            repeatedFor.forEach((unit, varIds) -> {
                int l = unit.k1, r = unit.k2;
                writeFor(l + 1, r - 1, varIds.size());
                varIds.forEach((varId) -> {
                    writeByte(3);
                    writeDataExpr(varId);
                });
            });
        } else {
            posToVarId.forEach(this::writeNormal);
        }
        varIdToPos = null;
        posToVarId = null;
        repeatedFor = null;
    }

    private void writeNormal(int arg1, int varId) {
        writeByte(1); //switch 普通赋值
        writeByte(2); // [%d] = ...
        writeInt(arg1);
        writeDataExpr(varId);
    }

    private void writeFor(int begin, int end, int itemsSize) {
        writeByte(2); //switch for
        writeInt(begin, end);
        writeInt(itemsSize); //repeatSize
    }
}
