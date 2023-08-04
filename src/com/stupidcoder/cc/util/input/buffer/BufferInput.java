package com.stupidcoder.cc.util.input.buffer;

import com.stupidcoder.cc.util.input.ILexerInput;
import com.stupidcoder.cc.util.input.buffer.readers.StringByteReader;

import java.nio.charset.StandardCharsets;

/**
 * 根据《编译原理》设计的输入系统，用于词法分析
 * @author stupid_coder_jyy
 */
public class BufferInput implements ILexerInput {
    private final Buffer BUFFER_A, BUFFER_B;

    /**
     * 单个词素的最大长度
     */
    private final int maxLexeme;

    /**
     * 指向词素的第一个字符
     */
    private int lexemeStart;

    /**
     * 指向下一个被读入的字符
     */
    private int forward;

    /**
     * 词素结束位置，即词素最后一个字符的下一个位置
     */
    private int lexemeEnd;

    /**
     * 是否处于开启的状态
     */
    private boolean isOpen;

    public static BufferInput fromString(String str) {
        return new BufferInput(64, new StringByteReader(str));
    }

    public BufferInput(IByteReader reader) {
        BUFFER_A = new Buffer(reader);
        BUFFER_B = new Buffer(reader);
        maxLexeme = BUFFER_A.size() / 2;
    }

    public BufferInput(int bufferSize, IByteReader reader) {
        BUFFER_A = new Buffer(bufferSize, reader);
        BUFFER_B = new Buffer(bufferSize, reader);
        maxLexeme = BUFFER_A.size() / 2;
    }

    @Override
    public void open() {
        if (isOpen) {
            return;
        }
        BUFFER_A.open();
        BUFFER_B.open();
        BUFFER_A.load();
        lexemeStart = 0;
        lexemeEnd = 0;
        forward = 0;
        isOpen = true;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void close() {
        BUFFER_A.close();
        BUFFER_B.close();
        isOpen = false;
    }

    @Override
    public byte next() {
        checkAvailable();
        byte result = forward < BUFFER_A.size() ?
                BUFFER_A.get(forward) :
                BUFFER_B.get(forward - BUFFER_A.size());
        forward++;
        if (BUFFER_A.available(forward) || BUFFER_B.available(forward)) {
            //如果没有到达边界，则直接返回
            return result;
        }
        //如果到达边界，则加载另外一个缓冲区
        if (forward == BUFFER_A.size() || forward == BUFFER_A.size() + BUFFER_B.size()) {
            flush();
            if (forward == BUFFER_A.size() + BUFFER_B.size()) {
                forward = 0;
            }
        }
        return result;
    }

    /**
     * 获得缓冲区中的词素，预读阶段仍然返回原有的词素
     * @param finish 是否结束当前词素的解析，若结束，则会调整lexemeStart
     * @return 词素
     */
    public String lexeme(boolean finish) {
        checkOpen();
        int len;
        byte[] temp;
        if (lexemeEnd < lexemeStart) {
            //forward在A，lexemeStart在B
            len = lexemeEnd + BUFFER_A.size() + BUFFER_B.size() - lexemeStart;
            temp = new byte[len];
            int lenA = BUFFER_A.size() + BUFFER_B.size() - lexemeStart;
            //复制A中的部分
            System.arraycopy(BUFFER_A.data(), 0, temp, 0, len - lenA);
            //复制B中的部分
            System.arraycopy(BUFFER_B.data(), lexemeStart - BUFFER_A.size(), temp, len - lenA, lenA);
        } else if (lexemeStart < BUFFER_A.size()){
            len = lexemeEnd - lexemeStart;
            temp = new byte[len];
            if (lexemeEnd < BUFFER_A.size()) {
                //lexemeEnd和lexemeStart在同一个缓冲区中
                System.arraycopy(BUFFER_A.data(), lexemeStart, temp, 0 ,len);
            } else {
                //lexemeEnd在B中，而lexemeStart在A中
                //复制A中的部分
                int lenA = BUFFER_A.size() - lexemeStart; //A中部分长度
                System.arraycopy(BUFFER_A.data(), lexemeStart, temp, 0 , lenA);
                //复制B中的部分
                System.arraycopy(BUFFER_B.data(), 0, temp, lenA, len - lenA);
            }
        } else {
            //lexemeStart和lexemeEnd都在B中
            len = lexemeEnd - lexemeStart;
            temp = new byte[len];
            System.arraycopy(BUFFER_B.data(), lexemeStart - BUFFER_A.size(), temp, 0, len);
        }
        if (finish) {
            lexemeStart = lexemeEnd;
        }
        return new String(temp, StandardCharsets.UTF_8);
    }

    @Override
    public boolean available() {
        checkOpen();
        return hasNext() && checkLexemeLength();
    }

    @Override
    public boolean hasNext() {
        return BUFFER_A.available(forward) || BUFFER_B.available(forward - BUFFER_A.size());
    }

    @Override
    public String lexeme() {
        return lexeme(true);
    }

    @Override
    public void markLexemeStart() {
        lexemeStart = forward;
    }

    /**
     * 将forward回退若干字符。预读状态下，最多回退到lexemeEnd处；非预读情况下，最多回退到lexemeStart处
     * @param count 回退的个数
     */
    @Override
    public void retract(int count) {
       forward = retract(count, lexemeStart);
       lexemeEnd = forward;
    }

    private int retract(int count, int limitVal) {
        if (forward < limitVal) {
            //forward在A中，lexemeStart在B中
            return count > forward ?
                    Math.max(limitVal, BUFFER_A.size() + BUFFER_B.size() - (count - forward)) : //最多退回到limitVal
                    forward - count; //没有退到B
        } else {
            return Math.max(limitVal, forward - count);
        }
    }


    /**
     * 填充另一个空闲的缓冲区。当向前看位置到达缓冲区末尾时，需要填充第二个缓冲区
     */
    private void flush() {
        if (lexemeStart < BUFFER_A.size() && forward > BUFFER_A.size()) {
            //理论上来说，不可能出现两个缓冲区同时被占用的情况
            throw new IllegalStateException("无法更新缓冲区：两个缓冲区同时被占用");
        }

        if (lexemeStart < BUFFER_A.size()) {
            //主缓冲区是A，则只可能加载B
            BUFFER_B.load();
        } else {
            //主缓冲区是B
            BUFFER_A.load();
        }
    }

    /**
     * 检查词素长度是否超标
     * @return 已达到最大限制则返回false
     */
    private boolean checkLexemeLength() {
        if (forward < lexemeStart) {
            return forward + (BUFFER_B.size() + BUFFER_A.size() - lexemeStart) < maxLexeme;
        }
        return forward - lexemeStart < maxLexeme;
    }
}