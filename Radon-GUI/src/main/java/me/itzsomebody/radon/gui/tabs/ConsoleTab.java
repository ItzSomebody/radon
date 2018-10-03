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

package me.itzsomebody.radon.gui.tabs;

import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * A {@link JPanel} containing a {@link JTextArea} which all System.out and System.err is redirected to.
 *
 * @author ItzSomebody
 */
public class ConsoleTab extends JPanel {
    /**
     * The {@link JTextArea} System.out and System.err is redirected to.
     */
    private JTextArea consoleTextArea;

    public ConsoleTab() {
        GridBagLayout gbl_this = new GridBagLayout();
        gbl_this.columnWidths = new int[]{0, 0};
        gbl_this.rowHeights = new int[]{0, 0};
        gbl_this.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_this.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        this.setBorder(new TitledBorder("Console"));
        this.setLayout(gbl_this);

        JScrollPane consoleScrollPane = new JScrollPane();
        GridBagConstraints gbc_consoleScrollPane = new GridBagConstraints();
        gbc_consoleScrollPane.fill = GridBagConstraints.BOTH;
        gbc_consoleScrollPane.gridx = 0;
        gbc_consoleScrollPane.gridy = 0;
        this.add(consoleScrollPane, gbc_consoleScrollPane);

        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        consoleScrollPane.setViewportView(consoleTextArea);

        PrintStream customPrintStream = new PrintStream(new OutputStreamRedirect(consoleTextArea));
        System.setOut(customPrintStream);
        System.setErr(customPrintStream);
    }

    /**
     * Custom {@link OutputStream}.
     */
    class OutputStreamRedirect extends OutputStream {
        /**
         * {@link JTextArea} System.out and System.err is redirected to.
         */
        private JTextArea consoleOutput;

        private OutputStreamRedirect(JTextArea consoleOutput) {
            this.consoleOutput = consoleOutput;
        }

        @Override
        public void write(int b) {
            this.consoleOutput.append(String.valueOf((char) b));
            this.consoleOutput.setCaretPosition(this.consoleOutput.getDocument().getLength());
        }
    }

    /**
     * Clears the {@link JTextArea} System.out and System.err is redirected to.
     */
    public void resetConsole() {
        consoleTextArea.setText(null);
    }
}
