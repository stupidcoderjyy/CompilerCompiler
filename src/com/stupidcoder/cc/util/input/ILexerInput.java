package com.stupidcoder.cc.util.input;

import java.io.Closeable;

/**
 * 用于词法分析的输入系统，这样操作能够灵活地改变输入方式和输入系统的实现方式
 * @author stupid_coder_jyy
 */
public interface ILexerInput extends Closeable {

    /**
     * 开启输入器
     */
    void open();

    /**
     * 判断输入系统是否启动
     * @return 启动返回true
     */
    boolean isOpen();

    /**
     * 是否可以继续向后取字符，即是否可以调用 {@link ILexerInput#next()}
     * @return 可以则返回true
     */
    boolean available();

    /**
     * 获得下一个字节
     * @return 下一个字节
     */
    byte next();

    /**
     * 是否还可能读入字符（是否到达输入流结尾）
     * @return 不能再读入字符返回true
     */
    boolean hasNext();

    /**
     * 获得完整的词素，同时更新词素起点
     * @return 词素
     */
    String lexeme();

    /**
     * 手动更新词素起点
     */
    void markLexemeStart();

    /**
     * 回退若干字符
     * @param count 回退的个数
     */
    void retract(int count);

    /**
     * 回退一个字符
     */
    default void retract() {
        retract(1);
    }

    /**
     * 跳过空格和制表符
     */
    default void skipSpaceAndTab() {
        byte nb;
        while (true) {
            if (!available()) {
                if (!hasNext()) {
                    return;
                }
                lexeme();
            }
            nb = next();
            if (nb != ' ' && nb != '\t') {
                retract();
                lexeme();
                return;
            }
        }
    }

    default void skipSpaceTabLineBreak(){
        byte nb;
        while (true) {
            if (!available()) {
                if (!hasNext()) {
                    return;
                }
                lexeme();
            }
            nb = next();
            if (nb == '\r') {
                next();
                continue;
            }

            if (nb != ' ' && nb != '\t') {
                retract();
                lexeme();
                return;
            }
        }
    }

    default void checkOpen() {
        if (!isOpen()) {
            throw new IllegalStateException("closed");
        }
    }

    default void checkAvailable() {
        if (!available()) {
            throw new IllegalStateException("not available");
        }
    }
}
