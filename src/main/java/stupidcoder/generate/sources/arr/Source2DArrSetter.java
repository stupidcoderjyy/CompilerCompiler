package stupidcoder.generate.sources.arr;

import stupidcoder.util.OpenRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Source2DArrSetter extends SourceArrSetter {
    // fold
    private Map<Unit, OpenRange> setters = new HashMap<>();
    // fold disabled
    private Map<Unit, Integer> posToVarId = new HashMap<>();
    private Map<Unit, List<Unit>> repeatedFor = new HashMap<>();

    public Source2DArrSetter(String srcId) {
        super(srcId);
    }

    public Source2DArrSetter(String srcId, int flag) {
        super(srcId, flag);
    }

    public void set(int arg1, int arg2, String data, IArrDataSourceSetter setter) {
        int varId = dataToVarId.compute(data, (k, v) -> {
            if (v == null) {
                varIdToData.add(data);
                if (isOpen(EXTRACT_COMMON_DATA)) {
                    repeatCount.add(1);
                }
                varIdToSetter.add(setter);
                return maxVarId++;
            } else if (isOpen(EXTRACT_COMMON_DATA)) {
                repeatCount.set(v, repeatCount.get(v) + 1);
            }
            varIdToSetter.set(v, setter);
            return v;
        });
        if (isOpen(FOLD_OPTIMIZE)) {
            setters.compute(new Unit(arg1, varId), (k, v) -> v == null ? OpenRange.empty() : v).union(arg2);
        } else {
            posToVarId.put(new Unit(arg1, arg2), varId);
        }
    }

    public void set(int arg1, int arg2, String data) {
        set(arg1, arg2, data, globalSetter);
    }

    public void set(int arg1, int arg2, int data, IArrDataSourceSetter setter) {
        set(arg1, arg2, Integer.toString(data), setter);
    }

    public void set(int arg1, int arg2, int data) {
        set(arg1, arg2, data, globalSetter);
    }

    @Override
    public void finish() {
        if (setters == null) {
            return;
        }
        if (isOpen(EXTRACT_COMMON_DATA)) {
            //定义变量
            for (int varId = 0; varId < repeatCount.size(); varId++) {
                if (repeatCount.get(varId) == 1) {
                    continue;
                }
                writeByte(0); //switch 变量定义
                writeInt(varId); //变量id
                varIdToSetter.get(varId).setSource(this, varIdToData.get(varId));
            }
        }
        if (isOpen(FOLD_OPTIMIZE)) {
            //输出普通设置
            setters.forEach((unit, range) -> range.forEach((l, r) -> {
                switch (r - l) {
                    case 3 -> {
                        writeNormal(unit.k1, l + 1, unit.k2);
                        writeNormal(unit.k1, l + 2, unit.k2);
                    }
                    case 2 -> writeNormal(unit.k1, l + 1, unit.k2);
                    default -> repeatedFor.compute(new Unit(l, r), (uFor, list) -> {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add(unit);
                        return list;
                    });
                }
            }));
            //输出for设置
            repeatedFor.forEach(((uFor, units) -> {
               writeFor(uFor.k1 + 1, uFor.k2 - 1, units.size());
                for (Unit u : units) {
                    writeByte(1);
                    writeInt(u.k1);
                    writeDataExpr(u.k2);
                }
            }));
        } else {
            posToVarId.forEach((pos, varId) -> writeNormal(pos.k1, pos.k2, varId));
        }
        this.posToVarId = null;
        this.repeatedFor = null;
        this.setters = null;
    }

    private void writeNormal(int arg1, int arg2, int varId) {
        writeByte(1); //switch 普通赋值
        writeByte(0);
        writeInt(arg1, arg2);
        writeDataExpr(varId);
    }

    private void writeFor(int begin, int end, int itemsSize) {
        writeByte(2); //switch for
        writeInt(begin, end);
        writeInt(itemsSize); //repeatSize
    }
}
