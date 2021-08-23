package com.vaxtomis.yaml4test.Tokenizer;

import com.vaxtomis.yaml4test.Tokenizer.StreamBuffer;
import com.vaxtomis.yaml4test.YamlFactory;
import org.junit.Test;

/**
 * @author vaxtomis
 */
public class StreamBufferTest {
    StreamBuffer streamBuffer = StreamBuffer.getInstance(
            YamlFactory.class.getClassLoader().getResource("").getPath() + "streamBufferTest.yml");
    @Test
    public void peek() {
        int i = 0;
        while (!streamBuffer.isFlagEndOfFile()) {
            System.out.print(streamBuffer.peek(i++));
        }
    }

    @Test
    public void preSub() {
        for (int i = 0; i<5; i++) {
            System.out.println(streamBuffer.preSub(i));
        }
    }

    @Test
    public void preForward() {
        while (!streamBuffer.isFlagEndOfFile()) {
            System.out.print(streamBuffer.preForward(5));
        }
    }

    @Test
    public void forward() {
        while (!streamBuffer.isFlagEndOfFile()) {
            System.out.print(streamBuffer.peek(0));
            streamBuffer.forward(1);
        }
    }
}
