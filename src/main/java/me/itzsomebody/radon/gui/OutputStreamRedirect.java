/*
 * Copyright (C) 2018 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.itzsomebody.radon.gui;

import java.io.OutputStream;
import javax.swing.*;

/**
 * Redirects all PrintStreams to the provided JTextArea.
 *
 * @author ItzSomebody
 */
public class OutputStreamRedirect extends OutputStream {
    /**
     * Target JTextArea to log everything to.
     */
    private JTextArea consoleOutput;

    /**
     * Constructor to create an OutputStreamRedirect.
     *
     * @param consoleOutput target JTextArea to log everything to.
     */
    public OutputStreamRedirect(JTextArea consoleOutput) {
        this.consoleOutput = consoleOutput;
    }

    /**
     * Writes the specified byte to this output stream.
     *
     * @param b the byte code.
     */
    @Override
    public void write(int b) {
        this.consoleOutput.append(String.valueOf((char) b));
        this.consoleOutput.setCaretPosition(this.consoleOutput.getDocument().getLength());
    }
}
