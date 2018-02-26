/**
 * Copyright (c) 2008, http://www.snakeyaml.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yaml.snakeyaml.reader;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.scanner.Constant;

/**
 * Reader: checks if code points are in allowed range. Returns '\0' when end of
 * data has been reached.
 */
public class StreamReader {
    private String name;
    private final Reader stream;
    private int pointer = 0; //in characters
    private boolean eof = true;
    private char[] buffer;
    private int index = 0; //in characters
    private int line = 0;
    private int column = 0; //in code points
    private char[] data;

    private static final int BUFFER_SIZE = 1025;

    public StreamReader(String stream) {
        this.name = "'string'";
        this.buffer = stream.toCharArray();
        this.stream = null;
        this.eof = true;
        this.data = null;
    }

    public StreamReader(Reader reader) {
        this.name = "'reader'";
        this.buffer = new char[0];
        this.stream = reader;
        this.eof = false;
        this.data = new char[BUFFER_SIZE];
    }

    private int codePointAt(char[] data, int offset) {
        final int codePoint = Character.codePointAt(data, offset, data.length);
        if (!isPrintable(codePoint)) {
            throw new ReaderException(name, offset, codePoint,
                    "special characters are not allowed");
        }
        return codePoint;
    }

    public static boolean isPrintable(final String data) {
        final int length = data.length();
        for (int offset = 0; offset < length; ) {
            final int codePoint = data.codePointAt(offset);

            if (!isPrintable(codePoint)) {
                return false;
            }

            offset += Character.charCount(codePoint);
        }

        return true;
    }

    public static boolean isPrintable(final int c) {
        return (c >= 0x20 && c <= 0x7E) || c == 0x9 || c == 0xA || c == 0xD || c == 0x85
                || (c >= 0xA0 && c <= 0xD7FF) || (c >= 0xE000 && c <= 0xFFFD)
                || (c >= 0x10000 && c <= 0x10FFFF);
    }

    public Mark getMark() {
        return new Mark(name, this.index, this.line, this.column, this.buffer, this.pointer);
    }

    public void forward() {
        forward(1);
    }

    /**
     * read the next length characters and move the pointer.
     * if the last character is high surrogate one more character will be read
     *
     * @param length amount of characters to move forward
     */
    public void forward(int length) {
        int c;
        for (int i = 0; i < length && ensureEnoughData(); i++) {
            c = codePointAt(buffer, pointer);
            this.pointer += Character.charCount(c);
            this.index += Character.charCount(c);
            if (Constant.LINEBR.has(c) || (c == '\r' && buffer[pointer] != '\n')) {
                this.line++;
                this.column = 0;
            } else if (c != 0xFEFF) {
                this.column++;
            }
        }
    }

    public int peek() {
        return (ensureEnoughData()) ? codePointAt(this.buffer, this.pointer) : '\0';
    }

    /**
     * Peek the next index-th code point
     *
     * @param index to peek
     * @return the next index-th code point
     */
    public int peek(int index) {
        int offset = 0;
        int nextIndex = 0;
        int codePoint = '\0';
        do {
            if (!ensureEnoughData(offset)) {
                return '\0';
            }
            codePoint = codePointAt(this.buffer, this.pointer + offset);
            offset += Character.charCount(codePoint);
            nextIndex++;
        } while (nextIndex <= index);

        return codePoint;
    }

    /**
     * peek the next length code points
     *
     * @param length amount of the characters to peek
     * @return the next length code points
     */
    public String prefix(int length) {
        int offset = 0;
        for (int resultLength = 0; resultLength < length
                && ensureEnoughData(offset); resultLength++) {
            int c = codePointAt(this.buffer, this.pointer + offset);
            offset += Character.charCount(c);
        }

        return new String(this.buffer, pointer, offset);
    }

    /**
     * prefix(length) immediately followed by forward(length)
     * @param length amount of characters to get
     * @return the next length code points
     */
    public String prefixForward(int length) {
        final String prefix = prefix(length);
        this.pointer += prefix.length();
        this.index += prefix.length();
        // prefix never contains new line characters
        this.column += length;
        return prefix;
    }

    private boolean ensureEnoughData() {
        return ensureEnoughData(0);
    }

    private boolean ensureEnoughData(int size) {
        if (!eof && pointer + size >= buffer.length) {
            try {
                int converted = stream.read(data, 0, BUFFER_SIZE - 1);
                if (converted > 0) {
                    if (Character.isHighSurrogate(data[converted - 1])) {
                        if (stream.read(data, converted, 1) == -1) {
                            eof = true;
                        } else {
                            converted++;
                        }
                    }

                    char[] newBuffer = Arrays.copyOfRange(buffer,
                            pointer,
                            buffer.length + converted);
                    System.arraycopy(data, 0, newBuffer, (buffer.length - pointer), converted);

                    buffer = newBuffer;
                    pointer = 0;
                } else {
                    eof = true;
                }
            } catch (IOException ioe) {
                throw new YAMLException(ioe);
            }
        }
        return (this.pointer + size) < buffer.length;
    }

    public int getColumn() {
        return column;
    }

    public Charset getEncoding() {
        return Charset.forName(((UnicodeReader) this.stream).getEncoding());
    }

    public int getIndex() {
        return index;
    }

    public int getLine() {
        return line;
    }
}
