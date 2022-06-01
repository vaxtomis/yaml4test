package com.vaxtomis.yaml4test.tokenizer;

import java.io.*;

/**
 * @description Used to process the data stream.
 * @note -> Reference Project: com.esotericsoftware.yamlbeans
 * (Copyright (c) 2008 Nathan Sweet, Copyright (c) 2006 Ola Bini)
 **/
class StreamBuffer implements BufferOperate {
    private final Reader reader;
    private final StringBuilder builder;
    private boolean flagEndOfFile;
    private int cursor;
    private int cno = 0;
    private int lno = 0;

    public StreamBuffer(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("<!--- Reader is null. !--->");
        }
        if (!(reader instanceof BufferedReader)) {
            reader = new BufferedReader(reader);
        }
        this.flagEndOfFile = false;
        this.reader = reader;
        this.builder = new StringBuilder();
        this.cursor = 0;
    }

    public StreamBuffer(String yaml) throws FileNotFoundException {
        this(new FileReader(yaml));
    }


    public static StreamBuffer getInstance(String yaml) {
        try {
            return new StreamBuffer(yaml);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Flush stream into builder.
     * @param length
     * @return null
     */
    private void flushIn(int length) {
        builder.delete(0,cursor);
        cursor = 0;
        while (builder.length() < length) {
            if (!flagEndOfFile) {
                int readLength = 0;
                char[] data = new char[1024];
                try{
                    readLength = reader.read(data);
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
                if (readLength == -1) {
                    flagEndOfFile = true;
                } else {
                    builder.append(String.valueOf(data,0,readLength));
                }
            }
            if (flagEndOfFile) {
                builder.append('\0');
                break;
            }
        }
    }

    /**
     * Peek the most recent (offset)th character.
     * @param offset
     * @return char
     */
    public char peek (int offset) {
        if (cursor + offset >= builder.length()) {
            flushIn(1 + offset);
        }
        //System.out.println(builder.charAt(cursor + offset));
        return builder.charAt(cursor + offset);
    }

    /**
     * Get the n-length part after the cursor
     * and return to the end of the buffer if it exceeds.
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
     *
     * @param length
     * @return String
     */
    public String preSub (int length) {
        if (cursor + length >= builder.length()) {
            flushIn(length);
        }
        if (cursor + length > builder.length()) {
            return builder.substring(cursor, builder.length());
        }
        return builder.substring(cursor, cursor + length);
    }

    /**
     * preSub and forward.
     */
    public String preForward(int length) {
        String buff = preSub(length);
        int start = cursor;
        for (char ch: buff.toCharArray()) {
            cursor++;
            if (Define.LINEBREAK.indexOf(ch) != -1 ||
                    ch == '\r' && buff.charAt(cursor - start) != '\n') {
                cno = 0;
                lno++;
            } else if (ch != '\uFEFF') cno++;
        }
        return buff;
    }

    /**
     * The cursor advances, and if it reaches the end of the builder, reads new content.
     * @param length
     * @return null
     */
    public void forward(int length) {
        if(cursor + 1 + length >= builder.length()){
            flushIn(1 + length);
        }
        char ch = 0;
        for (int i = 0; i<length; i++) {
            ch = builder.charAt(cursor);
            cursor++;
            if (Define.LINEBREAK.indexOf(ch) != -1 || ch == '\r' && builder.charAt(cursor) != '\n') {
                lno++;
                cno = 0;
            } else if (ch != '\uFEFF') {
                cno++;
            }
        }
    }

    public int spacesCount() {
        int length = 0;
        while (peek(length) == ' ' || peek(length) == '\t') {
            length++;
        }
        return length;
    }

    public String ch(char ch) {
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

    public boolean isFlagEndOfFile() {
        return flagEndOfFile;
    }
}
