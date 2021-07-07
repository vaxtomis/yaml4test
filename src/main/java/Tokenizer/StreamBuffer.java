package Tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @description: Tokenizer.StreamBuffer is used to process the data stream.
 **/
public class StreamBuffer {
    private static StreamBuffer streamBuffer = null;
    private final Reader reader;
    private final StringBuilder builder;
    private boolean flagEndOfFile;
    private int cursor;
    private int cno;
    private int lno;

    public StreamBuffer(Reader reader){
        if (reader == null) throw new IllegalArgumentException("<!--- Reader is null. !--->");
        if (!(reader instanceof BufferedReader)) reader = new BufferedReader(reader);
        this.flagEndOfFile = false;
        this.reader = reader;
        this.builder = new StringBuilder();
        this.cursor = 0;
        this.cno = 0;
        this.lno = 0;
    }

    public StreamBuffer(String yaml){
        this(new StringReader(yaml));
    }


    public static StreamBuffer getInstance(String yaml){
        if(streamBuffer == null){
            try {
                streamBuffer = new StreamBuffer(yaml);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return streamBuffer;
    }

    /**
     * Flush stream into builder.
     * @param length
     */
    public void flushIn(int length){
        builder.delete(0,cursor);
        cursor = 0;
        while(builder.length() < length){
            if(!flagEndOfFile){
                int readLength = 0;
                char[] data = new char[1024];
                try{
                    readLength = reader.read(data);
                } catch(IOException ioe){
                    ioe.printStackTrace();
                }
                if(readLength == -1){
                    flagEndOfFile = true;
                } else{
                    builder.append(String.valueOf(data,0,readLength));
                }
            }
            if(flagEndOfFile){
                builder.append('\0');
                break;
            }
        }
    }

    /**
     * Peek the most recent character.
     * @return char
     **/
    public char peek (){
        return peek(0);
    }

    /**
     * Peek the most recent (offset)th character.
     */
    public char peek (int offset){
        if(cursor + offset >= builder.length()){
            flushIn(1 + offset);
        }
        return builder.charAt(cursor + offset);
    }

    /**
     * Get the n-length part after the cursor and return to the end of the buffer if it exceeds.
     *
     * [--cursor--][----length=7----]
     *            56               12
     * [-----builder.length-----]
     *                         10
     * return substring(5,10)
     *
     *
     * [--cursor--][-length=3-]
     *            56          8
     * [-----builder.length-----]
     *                         10
     * return substring(5,8)
     */
    public String preSub (int length){
        if(cursor + length >= builder.length()){
            flushIn(length);
        }
        if(cursor + length > builder.length()){
            return builder.substring(cursor, builder.length());
        }
        return builder.substring(cursor, cursor + length);
    }

    public String preForward(int length){
        int start = cursor;
        String buff = preSub(length);
        for(char ch: buff.toCharArray()){
            cursor++;
            if(Define.LINEBREAK.indexOf(ch) != -1 ||
                    ch == '\r' && buff.charAt(cursor - start) != '\n'){
                cno = 0;
                lno++;
            } else if (ch != '\uFEFF') cno++;
        }
        return buff;
    }

    /**
     * The cursor advances, and if it reaches the end of the builder, reads new content.
     * @return null
     */
    public void forward(){
        forward(1);
    }

    public void forward(int length){
        if(cursor + 1 + length >= builder.length()){
            flushIn(1 + length);
        }
        char ch = 0;
        for(int i = 0; i<length; i++){
            ch = builder.charAt(cursor);
            cursor++;
            if(Define.LINEBREAK.indexOf(ch) != -1 || ch == '\r' && builder.charAt(cursor) != '\n'){
                lno++;
                cno = 0;
            } else if (ch != '\uFEFF'){
                cno++;
            }
        }
    }

    public int spacesCount(){
        int length = 0;
        while (peek(length) == ' ' || peek(length) == '\t') {
            length++;
        }
        return length;
    }

    public String ch(char ch){
        return "'" +
                ch +
                "' (" +
                (int) ch +
                ")";
    }

    public int getCno() {
        return cno;
    }

    public int getLno() {
        return lno;
    }
}
