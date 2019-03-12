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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import me.itzsomebody.radon.ObfuscationConfiguration;

/**
 * A {@link JPanel} used to set the input, output and libraries.
 *
 * @author ItzSomebody
 */
public class InputOutputTab extends JPanel {
    private JTextField inputField;
    private JTextField outputField;
    private DefaultListModel<String> libraryList;
    private File lastPath;

    public InputOutputTab() {
        GridBagLayout gbl_this = new GridBagLayout();
        gbl_this.columnWidths = new int[]{0, 0};
        gbl_this.rowHeights = new int[]{0, 378, 0};
        gbl_this.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_this.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        this.setLayout(gbl_this);

        JPanel inputOutputPanel = new JPanel();
        GridBagConstraints gbc_inputOutputPanel = new GridBagConstraints();
        gbc_inputOutputPanel.insets = new Insets(0, 0, 5, 0);
        gbc_inputOutputPanel.fill = GridBagConstraints.BOTH;
        gbc_inputOutputPanel.gridx = 0;
        gbc_inputOutputPanel.gridy = 0;
        inputOutputPanel.setBorder(new TitledBorder("Input-Output"));
        this.add(inputOutputPanel, gbc_inputOutputPanel);

        GridBagLayout gbl_inputOutputPanel = new GridBagLayout();
        gbl_inputOutputPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_inputOutputPanel.rowHeights = new int[]{0, 0, 0};
        gbl_inputOutputPanel.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_inputOutputPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        inputOutputPanel.setLayout(gbl_inputOutputPanel);

        JLabel inputLabel = new JLabel("Input:");
        GridBagConstraints gbc_inputLabel = new GridBagConstraints();
        gbc_inputLabel.anchor = GridBagConstraints.EAST;
        gbc_inputLabel.insets = new Insets(5, 5, 5, 5);
        gbc_inputLabel.gridx = 0;
        gbc_inputLabel.gridy = 0;
        inputOutputPanel.add(inputLabel, gbc_inputLabel);

        this.inputField = new JTextField();
        GridBagConstraints gbc_inputField = new GridBagConstraints();
        gbc_inputField.gridwidth = 17;
        gbc_inputField.insets = new Insets(5, 0, 5, 5);
        gbc_inputField.fill = GridBagConstraints.BOTH;
        gbc_inputField.gridx = 1;
        gbc_inputField.gridy = 0;
        inputOutputPanel.add(inputField, gbc_inputField);
        inputField.setColumns(10);

        JButton inputButton = new JButton("Select");
        GridBagConstraints gbc_inputButton = new GridBagConstraints();
        gbc_inputButton.fill = GridBagConstraints.BOTH;
        gbc_inputButton.insets = new Insets(5, 0, 5, 5);
        gbc_inputButton.gridx = 18;
        gbc_inputButton.gridy = 0;
        inputButton.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            if (inputField.getText() != null && !inputField.getText().isEmpty()) {
                chooser.setSelectedFile(new File(inputField.getText()));
            }
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (lastPath != null)
                chooser.setCurrentDirectory(lastPath);
            int result = chooser.showOpenDialog(this);
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    inputField.setText(chooser.getSelectedFile().getAbsolutePath());
                    lastPath = chooser.getSelectedFile();
                });
            }
        });
        inputOutputPanel.add(inputButton, gbc_inputButton);

        JLabel outputLabel = new JLabel("Output:");
        GridBagConstraints gbc_outputLabel = new GridBagConstraints();
        gbc_outputLabel.anchor = GridBagConstraints.EAST;
        gbc_outputLabel.insets = new Insets(0, 5, 5, 5);
        gbc_outputLabel.gridx = 0;
        gbc_outputLabel.gridy = 1;
        inputOutputPanel.add(outputLabel, gbc_outputLabel);

        this.outputField = new JTextField();
        GridBagConstraints gbc_outputField = new GridBagConstraints();
        gbc_outputField.gridwidth = 17;
        gbc_outputField.insets = new Insets(0, 0, 5, 5);
        gbc_outputField.fill = GridBagConstraints.BOTH;
        gbc_outputField.gridx = 1;
        gbc_outputField.gridy = 1;
        inputOutputPanel.add(outputField, gbc_outputField);
        outputField.setColumns(10);

        JButton outputButton = new JButton("Select");
        GridBagConstraints gbc_outputButton = new GridBagConstraints();
        gbc_outputButton.fill = GridBagConstraints.BOTH;
        gbc_outputButton.insets = new Insets(0, 0, 5, 5);
        gbc_outputButton.gridx = 18;
        gbc_outputButton.gridy = 1;
        outputButton.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            if (outputField.getText() != null && !outputField.getText().isEmpty()) {
                chooser.setSelectedFile(new File(outputField.getText()));
            }
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (lastPath != null)
                chooser.setCurrentDirectory(lastPath);
            int result = chooser.showOpenDialog(this);
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    outputField.setText(chooser.getSelectedFile().getAbsolutePath());
                    lastPath = chooser.getSelectedFile();
                });
            }
        });
        inputOutputPanel.add(outputButton, gbc_outputButton);

        JPanel librariesPanel = new JPanel();
        GridBagConstraints gbc_librariesPanel = new GridBagConstraints();
        gbc_librariesPanel.fill = GridBagConstraints.BOTH;
        gbc_librariesPanel.gridx = 0;
        gbc_librariesPanel.gridy = 1;
        librariesPanel.setBorder(new TitledBorder("Libraries"));
        this.add(librariesPanel, gbc_librariesPanel);

        GridBagLayout gbl_librariesPanel = new GridBagLayout();
        gbl_librariesPanel.columnWidths = new int[]{500, 33};
        gbl_librariesPanel.rowHeights = new int[]{0, 0};
        gbl_librariesPanel.columnWeights = new double[]{1.0, 0.0};
        gbl_librariesPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        librariesPanel.setLayout(gbl_librariesPanel);

        JScrollPane librariesPane = new JScrollPane();
        GridBagConstraints gbc_librariesPane = new GridBagConstraints();
        gbc_librariesPane.insets = new Insets(0, 0, 0, 5);
        gbc_librariesPane.fill = GridBagConstraints.BOTH;
        gbc_librariesPane.gridx = 0;
        gbc_librariesPane.gridy = 0;
        librariesPanel.add(librariesPane, gbc_librariesPane);

        libraryList = new DefaultListModel<>();
        String jreHome = System.getProperty("java.home");
        if (jreHome != null) {
            libraryList.addElement(jreHome + "/lib/rt.jar");
            libraryList.addElement(jreHome + "/lib/jce.jar");
        }
        JList<String> librariesJList = new JList<>(libraryList);
        librariesPane.setViewportView(librariesJList);

        JPanel librariesButtonPanel = new JPanel();
        GridBagConstraints gbc_librariesButtonPanel = new GridBagConstraints();
        gbc_librariesButtonPanel.fill = GridBagConstraints.BOTH;
        gbc_librariesButtonPanel.gridx = 1;
        gbc_librariesButtonPanel.gridy = 0;
        librariesPanel.add(librariesButtonPanel, gbc_librariesButtonPanel);

        GridBagLayout gbl_librariesButtonPanel = new GridBagLayout();
        gbl_librariesButtonPanel.columnWidths = new int[]{0, 0};
        gbl_librariesButtonPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
        gbl_librariesButtonPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl_librariesButtonPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        librariesButtonPanel.setLayout(gbl_librariesButtonPanel);

        JButton librariesAddButton = new JButton("Add");
        GridBagConstraints gbc_librariesAddButton = new GridBagConstraints();
        gbc_librariesAddButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_librariesAddButton.insets = new Insets(0, 0, 5, 5);
        gbc_librariesAddButton.gridx = 0;
        gbc_librariesAddButton.gridy = 0;
        librariesAddButton.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            if (inputField.getText() != null && !inputField.getText().isEmpty()) {
                chooser.setSelectedFile(new File(inputField.getText()));
            }
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (lastPath != null)
                chooser.setCurrentDirectory(lastPath);
            int result = chooser.showOpenDialog(this);
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    for (File file : chooser.getSelectedFiles()) {
                        libraryList.addElement(file.getAbsolutePath());
                    }

                    lastPath = chooser.getSelectedFile();
                });
            }
        });
        librariesButtonPanel.add(librariesAddButton, gbc_librariesAddButton);

        JButton librariesRemoveButton = new JButton("Remove");
        GridBagConstraints gbc_librariesRemoveButton = new GridBagConstraints();
        gbc_librariesRemoveButton.insets = new Insets(0, 0, 5, 5);
        gbc_librariesRemoveButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_librariesRemoveButton.gridx = 0;
        gbc_librariesRemoveButton.gridy = 1;
        librariesRemoveButton.addActionListener((e) -> {
            List<String> removeList = librariesJList.getSelectedValuesList();
            if (removeList.isEmpty())
                return;

            for (String s : removeList) {
                libraryList.removeElement(s);
            }
        });
        librariesButtonPanel.add(librariesRemoveButton, gbc_librariesRemoveButton);

        JButton librariesResetButton = new JButton("Reset");
        GridBagConstraints gbc_librariesResetButton = new GridBagConstraints();
        gbc_librariesResetButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_librariesResetButton.insets = new Insets(0, 0, 5, 5);
        gbc_librariesResetButton.gridx = 0;
        gbc_librariesResetButton.gridy = 2;
        librariesResetButton.addActionListener((e) -> {
            libraryList.clear();

            String javaHome = System.getProperty("java.home");
            if (javaHome != null) {
                libraryList.addElement(javaHome + "/lib/rt.jar");
                libraryList.addElement(javaHome + "/lib/jce.jar");
            }
        });
        librariesButtonPanel.add(librariesResetButton, gbc_librariesResetButton);
    }

    /**
     * Gets and returns the specified input file path as a {@link String}.
     *
     * @return the specified input file path as a {@link String}.
     */
    public String getInputPath() {
        return this.inputField.getText();
    }

    /**
     * Gets and returns the specified output file path as a {@link String}.
     *
     * @return the specified output file path as a {@link String}.
     */
    public String getOutputPath() {
        return this.outputField.getText();
    }

    /**
     * Gets and returns the specified libraries as a {@link List<File>}.
     *
     * @return the specified libraries as a {@link List<File>}.
     */
    public List<File> getLibraries() {
        ArrayList<File> libs = new ArrayList<>();
        for (int i = 0; i < this.libraryList.size(); i++) {
            libs.add(new File(this.libraryList.get(i)));
        }

        return libs;
    }

    /**
     * Sets the tab settings accordingly with the provided {@link ObfuscationConfiguration}.
     *
     * @param info the {@link ObfuscationConfiguration} used to determine the tab setup.
     */
    public void setSettings(ObfuscationConfiguration info) {
        inputField.setText(null);
        outputField.setText(null);
        libraryList.clear();

        if (info.getInput() != null) {
            inputField.setText(info.getInput().getAbsolutePath());
        }
        if (info.getOutput() != null) {
            outputField.setText(info.getOutput().getAbsolutePath());
        }
        if (info.getLibraries() != null) {
            info.getLibraries().forEach(file -> libraryList.addElement(file.getAbsolutePath()));
        }
    }
}
