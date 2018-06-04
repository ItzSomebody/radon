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

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import javax.swing.*;
import me.itzsomebody.radon.Radon;

/**
 * Makes a "console" GUI so users can see log events when they use the
 * GUI rather than the CLI.
 *
 * @author ItzSomebody
 */
public class ConsoleGUI {
    /**
     * This GUI JFrame.
     */
    private JFrame thisFrame;

    /**
     * Just the panel.
     */
    private JPanel consolePanel;

    /**
     * Scroll pane for the JTextArea.
     */
    private JScrollPane consoleScrollPane;

    /**
     * Logging area.
     */
    private JTextArea consoleOutput;
    /**
     * Original PrintStream (System.out) so we can restore it later when needed.
     */
    private PrintStream stdOut;

    /**
     * Original PrintStream (System.err) so we can restore it later when needed.
     */
    private PrintStream stdErr;

    /**
     * Constructor to create a Console GUI.
     */
    public ConsoleGUI() {
        this.stdOut = System.out;
        this.stdErr = System.err;
        this.init();
    }

    /**
     * Initializes the JFrame and its contents.
     */
    private void init() {
        this.thisFrame = new JFrame();
        this.thisFrame.setTitle(Radon.PREFIX + " " + Radon.VERSION);
        this.thisFrame.setResizable(true);
        this.thisFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.thisFrame.setLocationRelativeTo(null);
        this.thisFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                resetPrintStream();
                thisFrame.dispose();
            }
        });
        this.thisFrame.setBounds(400, 400, 600, 400);

        this.consolePanel = new JPanel(new BorderLayout());
        this.consoleOutput = new JTextArea();
        this.consoleOutput.setFont(new Font("Arial", Font.PLAIN, 12));
        this.consoleOutput.setEditable(false);
        this.consoleScrollPane = new JScrollPane(this.consoleOutput);
        this.consolePanel.add(this.consoleScrollPane, "Center");
        this.thisFrame.getContentPane().add(this.consolePanel);

        PrintStream customPrintStream = new PrintStream(new OutputStreamRedirect(this.consoleOutput));
        System.setOut(customPrintStream);
        System.setErr(customPrintStream);

        this.thisFrame.setVisible(true);
    }

    /**
     * Restores the default PrintStreams when the logging window is closed
     */
    private void resetPrintStream() {
        System.setOut(this.stdOut);
        System.setErr(this.stdErr);
    }
}
