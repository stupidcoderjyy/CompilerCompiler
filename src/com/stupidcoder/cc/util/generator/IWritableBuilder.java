package com.stupidcoder.cc.util.generator;

/**
 * 代码生成器
 */
public interface IWritableBuilder {
        /**
     * 生成代码文件
     */
    default void gen(CodeWriter writer) {
        init();
        build(writer);
        clear();
    }

    /**
     * 计算加载类的各部件
     */
    void init();

    /**
     * 组装类结构
     */
    void build(CodeWriter generator);

    /**
     * 释放加载数据所需要的资源
     */
    void clear();
}
