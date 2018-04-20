package me.itzsomebody.radon.gui;

import me.itzsomebody.radon.Radon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

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
        this.thisFrame.setBounds(400, 400, 400, 400);

        this.consolePanel = new JPanel(new BorderLayout());
        this.consoleOutput = new JTextArea();
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
