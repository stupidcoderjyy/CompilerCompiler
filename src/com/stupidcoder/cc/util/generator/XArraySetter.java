package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XArraySetter extends XWritable{
    private final String arrName;
    private final String varType;
    private String varPrefix = "var";
    private int maxVarId = -1;
    private Map<Integer, Integer> targetToVar = new HashMap<>();
    private List<String> varToInitializer = new ArrayList<>();
    private Map<String, Integer> initializerToVar = new HashMap<>();
    private List<Integer> varRepeatedCount = new ArrayList<>();

    private final XWritableList listDefVal = new XWritableList();
    private final XWritableList listSetArr = new XWritableList().disableFinalLineBreak();

    public XArraySetter(String arrName, String varType) {
        this.arrName = arrName;
        this.varType = varType;
    }

    public XArraySetter(String arrName, String varType, String varPrefix) {
        this.arrName = arrName;
        this.varType = varType;
        this.varPrefix = varPrefix;
    }

    public void set(int i, String initializer) {
        int varId;
        if (initializerToVar.containsKey(initializer)) {
            varId = initializerToVar.get(initializer);
            varRepeatedCount.set(varId, varRepeatedCount.get(varId) + 1);
        } else {
            varId = ++maxVarId;
            initializerToVar.put(initializer, varId);
            varRepeatedCount.add(1);
            varToInitializer.add(initializer);
        }
        targetToVar.put(i, varId);
    }

    public void finish() {
        //变量定义
        for (int varId = 0; varId < varToInitializer.size(); varId++) {
            String initializer = varToInitializer.get(varId);
            if (varRepeatedCount.get(varId) > 1) {
                listDefVal.append("%s %s%d = %s;", varType, varPrefix, varId, initializer);
            }
        }
        //数组赋值
        targetToVar.forEach((target, varId) -> {
            String initializer = varRepeatedCount.get(varId) > 1 ?
                    varPrefix + varId :
                    varToInitializer.get(varId);
            listSetArr.append("%s[%d] = %s;", arrName, target, initializer);
        });
        targetToVar = null;
        varToInitializer = null;
        initializerToVar = null;
        varRepeatedCount = null;
    }

    @Override
    protected void setIndentations(int i) {
        super.setIndentations(i);
        listDefVal.setIndentations(i);
        listSetArr.setIndentations(i);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        listDefVal.output(writer);
        listSetArr.output(writer);
    }
}
