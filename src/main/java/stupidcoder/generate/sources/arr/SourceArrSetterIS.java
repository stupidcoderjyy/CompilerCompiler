package stupidcoder.generate.sources.arr;

import stupidcoder.generate.sources.SourceCached;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceArrSetterIS extends SourceCached {
    private int maxVarId = -1;
    private Map<Integer, Integer> setPosToVarId = new HashMap<>();
    private List<String> varIdToExpr = new ArrayList<>();
    private Map<String, Integer> exprToVarId = new HashMap<>();
    private List<Integer> varRepeatCount = new ArrayList<>();

    public SourceArrSetterIS(String name) {
        super(name);
    }

    public void set(int i, String expr) {
        int varId;
        if (exprToVarId.containsKey(expr)) {
            varId = exprToVarId.get(expr);
            varRepeatCount.set(varId, varRepeatCount.get(varId) + 1);
        } else {
            varId = ++maxVarId;
            exprToVarId.put(expr, varId);
            varRepeatCount.add(1);
            varIdToExpr.add(expr);
        }
        setPosToVarId.put(i, varId);
    }

    public void finish() {
        if (varIdToExpr == null) {
            return;
        }
        for (int varId = 0; varId < varIdToExpr.size(); varId++) {
            if (varRepeatCount.get(varId) == 1) {
                continue;
            }
            writeInt(0); //switch
            writeString("e" + varId);
            writeString(varIdToExpr.get(varId));
        }
        //数组赋值
        setPosToVarId.forEach((target, varId) -> {
            writeInt(1); //switch
            writeInt(target);
            writeString(varRepeatCount.get(varId) > 1 ?
                    "e" + varId :
                    varIdToExpr.get(varId));
        });
        setPosToVarId = null;
        varIdToExpr = null;
        exprToVarId = null;
        varRepeatCount = null;
    }

    @Override
    public void lock() {
        finish();
        super.lock();
    }
}
