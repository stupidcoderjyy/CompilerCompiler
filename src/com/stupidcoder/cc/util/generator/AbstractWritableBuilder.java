package com.stupidcoder.cc.util.generator;

/**
 * 代码生成器
 */
public abstract class AbstractWritableBuilder {
    public AbstractWritableBuilder() {
    }

    /**
     * 生成代码文件
     */
    public void genDefault() {
        init();
        buildTarget(CodeWriter.getGlobalInstance());
        clear();
    }

    /**
     * 计算加载类的各部件
     */
    protected abstract void init();

    /**
     * 组装类结构
     */
    protected abstract void buildTarget(CodeWriter generator);

    /**
     * 释放加载数据所需要的资源
     */
    protected abstract void clear();
}
