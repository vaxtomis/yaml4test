package com.vaxtomis.yaml4test.tokenizer;

/**
 * <p>
 * Operation interface to obtain the required value
 * from the buffer stream.
 * <br>
 * 从缓冲流中获取所需值的操作接口。
 * </p>
 */
interface BufferOperate {
    char peek(int offset);

    void forward(int length);

    String preSubstring(int length);

    String preForward(int length);

    int countSpaces();
}
