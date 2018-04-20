package me.itzsomebody.radon.gui;

import javax.swing.*;
import java.io.OutputStream;

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
