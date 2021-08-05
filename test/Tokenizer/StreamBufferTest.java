package Tokenizer;

import org.junit.Test;

/**
 * @author vaxtomis
 */
public class StreamBufferTest {
    StreamBuffer streamBuffer = StreamBuffer.getInstance("test/streamBufferTest.yml");
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
