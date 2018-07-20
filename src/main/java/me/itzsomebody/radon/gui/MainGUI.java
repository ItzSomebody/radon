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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import me.itzsomebody.radon.Radon;
import me.itzsomebody.radon.config.Config;
import me.itzsomebody.radon.config.ConfigEnum;
import me.itzsomebody.radon.config.ConfigWriter;
import me.itzsomebody.radon.internal.Bootstrap;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.transformers.flow.HeavyFlowObfuscation;
import me.itzsomebody.radon.transformers.flow.LightFlowObfuscation;
import me.itzsomebody.radon.transformers.flow.NormalFlowObfuscation;
import me.itzsomebody.radon.transformers.invokedynamic.HeavyInvokeDynamic;
import me.itzsomebody.radon.transformers.invokedynamic.LightInvokeDynamic;
import me.itzsomebody.radon.transformers.invokedynamic.NormalInvokeDynamic;
import me.itzsomebody.radon.transformers.linenumbers.ObfuscateLineNumbers;
import me.itzsomebody.radon.transformers.linenumbers.RemoveLineNumbers;
import me.itzsomebody.radon.transformers.localvariables.ObfuscateLocalVariables;
import me.itzsomebody.radon.transformers.localvariables.RemoveLocalVariables;
import me.itzsomebody.radon.transformers.misc.Crasher;
import me.itzsomebody.radon.transformers.misc.Expiry;
import me.itzsomebody.radon.transformers.misc.HideCode;
import me.itzsomebody.radon.transformers.misc.InnerClassRemover;
import me.itzsomebody.radon.transformers.misc.NumberObfuscation;
import me.itzsomebody.radon.transformers.misc.Shuffler;
import me.itzsomebody.radon.transformers.misc.StringPool;
import me.itzsomebody.radon.transformers.renamer.Renamer;
import me.itzsomebody.radon.transformers.sourcedebug.ObfuscateSourceDebug;
import me.itzsomebody.radon.transformers.sourcedebug.RemoveSourceDebug;
import me.itzsomebody.radon.transformers.sourcename.ObfuscateSourceName;
import me.itzsomebody.radon.transformers.sourcename.RemoveSourceName;
import me.itzsomebody.radon.transformers.stringencryption.HeavyStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.LightStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.NormalStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.VeryLightStringEncryption;
import me.itzsomebody.radon.utils.WatermarkUtils;


/**
 * This is pretty much all Eclipse WindowBuilder
 *
 * @author ItzSomebody
 * @author Eclipse WindowBuilder
 */
public class MainGUI {
    private JFrame frmRadonObfuscator;
    private JTextField inputField;
    private JTextField outputField;
    private JTextField exemptField;
    private JTextField trashChanceField;
    private JTextField waterMarkMessageField;
    private JTextField expirationDateField;
    private JTextField expirationMessageField;
    private JPasswordField watermarkPassword;
    private JTextField extractorInput;
    private JPasswordField extractorKey;
    private File lastPath;

    /**
     * Create the application.
     */
    public MainGUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            // exception.printStackTrace();
        }
        this.frmRadonObfuscator = new JFrame();
        this.frmRadonObfuscator.setTitle(Radon.PREFIX + " " + Radon.VERSION);
        this.frmRadonObfuscator.setSize(440, 570);
        this.frmRadonObfuscator.setLocationRelativeTo(null);
        this.frmRadonObfuscator.setResizable(true);
        this.frmRadonObfuscator
                .setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frmRadonObfuscator.getContentPane()
                .setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        this.frmRadonObfuscator.getContentPane().add(tabbedPane);

        JPanel panel_4 = new JPanel();
        tabbedPane.addTab("Files", null, panel_4, null);
        GridBagLayout gbl_panel_4 = new GridBagLayout();
        gbl_panel_4.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0};
        gbl_panel_4.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0};
        gbl_panel_4.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel_4.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, Double.MIN_VALUE};
        panel_4.setLayout(gbl_panel_4);

        JLabel lblInput = new JLabel("Input:");
        GridBagConstraints gbc_lblInput = new GridBagConstraints();
        gbc_lblInput.anchor = GridBagConstraints.EAST;
        gbc_lblInput.insets = new Insets(0, 0, 5, 5);
        gbc_lblInput.gridx = 1;
        gbc_lblInput.gridy = 1;
        panel_4.add(lblInput, gbc_lblInput);

        this.inputField = new JTextField();
        GridBagConstraints gbc_textField_1 = new GridBagConstraints();
        gbc_textField_1.gridwidth = 9;
        gbc_textField_1.insets = new Insets(0, 0, 5, 5);
        gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_1.gridx = 2;
        gbc_textField_1.gridy = 1;
        panel_4.add(this.inputField, gbc_textField_1);
        this.inputField.setColumns(10);

        JButton btnNewButton = new JButton("Select");
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton.gridx = 11;
        gbc_btnNewButton.gridy = 1;
        btnNewButton.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            if (inputField.getText() != null
                    && !inputField.getText().isEmpty()) {
                chooser.setSelectedFile(new File(inputField.getText()));
            }
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (lastPath != null)
                chooser.setCurrentDirectory(lastPath);
            int result = chooser.showOpenDialog(frmRadonObfuscator);
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    inputField.setText(chooser.getSelectedFile().getAbsolutePath());
                    lastPath = chooser.getSelectedFile();
                });
            }
        });
        panel_4.add(btnNewButton, gbc_btnNewButton);

        JLabel lblOutput = new JLabel("Output:");
        GridBagConstraints gbc_lblOutput = new GridBagConstraints();
        gbc_lblOutput.insets = new Insets(0, 0, 5, 5);
        gbc_lblOutput.anchor = GridBagConstraints.EAST;
        gbc_lblOutput.gridx = 1;
        gbc_lblOutput.gridy = 2;
        panel_4.add(lblOutput, gbc_lblOutput);

        this.outputField = new JTextField();
        GridBagConstraints gbc_textField_2 = new GridBagConstraints();
        gbc_textField_2.gridwidth = 9;
        gbc_textField_2.insets = new Insets(0, 0, 5, 5);
        gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_2.gridx = 2;
        gbc_textField_2.gridy = 2;
        panel_4.add(this.outputField, gbc_textField_2);
        this.outputField.setColumns(10);

        JButton btnNewButton_1 = new JButton("Select");
        GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
        gbc_btnNewButton_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton_1.insets = new Insets(0, 0, 5,
                5);
        gbc_btnNewButton_1.gridx = 11;
        gbc_btnNewButton_1.gridy = 2;
        btnNewButton_1.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            if (outputField.getText() != null
                    && !outputField.getText().isEmpty()) {
                chooser.setSelectedFile(new File(outputField.getText()));
            }
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (lastPath != null)
                chooser.setCurrentDirectory(lastPath);
            int result = chooser.showOpenDialog(frmRadonObfuscator);
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    outputField.setText(chooser.getSelectedFile().getAbsolutePath());
                    lastPath = chooser.getSelectedFile();
                });
            }
        });
        panel_4.add(btnNewButton_1, gbc_btnNewButton_1);

        JSeparator separator_2 = new JSeparator();
        GridBagConstraints gbc_separator_2 = new GridBagConstraints();
        gbc_separator_2.fill = GridBagConstraints.HORIZONTAL;
        gbc_separator_2.gridwidth = 13;
        gbc_separator_2.insets = new Insets(0, 0, 5, 0);
        gbc_separator_2.gridx = 0;
        gbc_separator_2.gridy = 3;
        panel_4.add(separator_2, gbc_separator_2);

        JLabel lblNewLabel_6 = new JLabel("Libraries:");
        GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
        gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_6.gridx = 1;
        gbc_lblNewLabel_6.gridy = 4;
        panel_4.add(lblNewLabel_6, gbc_lblNewLabel_6);

        JScrollPane scrollPane_2 = new JScrollPane();
        GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
        gbc_scrollPane_2.gridheight = 13;
        gbc_scrollPane_2.gridwidth = 9;
        gbc_scrollPane_2.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_2.gridx = 2;
        gbc_scrollPane_2.gridy = 4;
        panel_4.add(scrollPane_2, gbc_scrollPane_2);

        DefaultListModel<String> libList = new DefaultListModel<>();
        String jreHome = System.getProperty("java.home");
        if (jreHome != null) {
            libList.addElement(jreHome + "/lib/rt.jar");
            libList.addElement(jreHome + "/lib/jce.jar");
        }

        JList<String> list_2 = new JList<>(libList);
        list_2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scrollPane_2.setViewportView(list_2);

        JButton btnNewButton_6 = new JButton("Add");
        btnNewButton_6.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            if (inputField.getText() != null
                    && !inputField.getText().isEmpty()) {
                chooser.setSelectedFile(new File(inputField.getText()));
            }
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (lastPath != null)
                chooser.setCurrentDirectory(lastPath);
            int result = chooser.showOpenDialog(frmRadonObfuscator);
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    for (File file : chooser.getSelectedFiles()) {
                        libList.addElement(file.getAbsolutePath());
                    }

                    lastPath = chooser.getSelectedFile();
                });
            }
        });
        GridBagConstraints gbc_btnNewButton_6 = new GridBagConstraints();
        gbc_btnNewButton_6.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton_6.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton_6.gridx = 11;
        gbc_btnNewButton_6.gridy = 15;
        panel_4.add(btnNewButton_6, gbc_btnNewButton_6);

        JButton btnNewButton_7 = new JButton("Remove");
        btnNewButton_7.addActionListener((e) -> {
            List<String> removeList = list_2.getSelectedValuesList();
            if (removeList.isEmpty())
                return;

            for (String s : removeList) {
                libList.removeElement(s);
            }
        });
        GridBagConstraints gbc_btnNewButton_7 = new GridBagConstraints();
        gbc_btnNewButton_7.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton_7.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton_7.gridx = 11;
        gbc_btnNewButton_7.gridy = 16;
        panel_4.add(btnNewButton_7, gbc_btnNewButton_7);

        JPanel panel = new JPanel();
        tabbedPane.addTab("Obfuscation", null, panel, null);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 75, 0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setEnabled(false);
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox.gridx = 9;
        gbc_comboBox.gridy = 0;
        String[] encryptions = {"SuperLight", "Light", "Normal", "Heavy"};
        for (String s : encryptions) {
            comboBox.addItem(s);
        }
        panel.add(comboBox, gbc_comboBox);

        JCheckBox chckbxStringEncryption = new JCheckBox("String Encryption");
        GridBagConstraints gbc_chckbxStringEncryption = new GridBagConstraints();
        gbc_chckbxStringEncryption.anchor = GridBagConstraints.WEST;
        gbc_chckbxStringEncryption.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxStringEncryption.gridx = 0;
        gbc_chckbxStringEncryption.gridy = 0;
        chckbxStringEncryption.addActionListener((e) -> {
            if (chckbxStringEncryption.isSelected()) {
                comboBox.setEnabled(true);
            } else if (!chckbxStringEncryption.isSelected()) {
                comboBox.setEnabled(false);
            }
        });
        panel.add(chckbxStringEncryption, gbc_chckbxStringEncryption);

        JComboBox<String> comboBox_1 = new JComboBox<>();
        comboBox_1.setEnabled(false);
        GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
        gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_1.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox_1.gridx = 9;
        gbc_comboBox_1.gridy = 1;
        String[] invokeDynamics = {"Light", "Normal", "Heavy"};
        for (String s : invokeDynamics) {
            comboBox_1.addItem(s);
        }
        panel.add(comboBox_1, gbc_comboBox_1);

        JCheckBox chckbxInvokeDynamic = new JCheckBox("InvokeDynamic");
        GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
        gbc_chckbxNewCheckBox_1.anchor = GridBagConstraints.WEST;
        gbc_chckbxNewCheckBox_1.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxNewCheckBox_1.gridx = 0;
        gbc_chckbxNewCheckBox_1.gridy = 1;
        chckbxInvokeDynamic.addActionListener((e) -> {
            if (chckbxInvokeDynamic.isSelected()) {
                comboBox_1.setEnabled(true);
            } else if (!chckbxInvokeDynamic.isSelected()) {
                comboBox_1.setEnabled(false);
            }
        });

        panel.add(chckbxInvokeDynamic, gbc_chckbxNewCheckBox_1);

        JComboBox<String> comboBox_2 = new JComboBox<>();
        comboBox_2.setEnabled(false);
        GridBagConstraints gbc_comboBox_2 = new GridBagConstraints();
        gbc_comboBox_2.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_2.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox_2.gridx = 9;
        gbc_comboBox_2.gridy = 2;
        String[] flows = {"Light", "Normal", "Heavy"};
        for (String s : flows) {
            comboBox_2.addItem(s);
        }
        panel.add(comboBox_2, gbc_comboBox_2);

        JCheckBox chckbxFlow = new JCheckBox("Flow");
        GridBagConstraints gbc_chckbxFlow = new GridBagConstraints();
        gbc_chckbxFlow.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxFlow.anchor = GridBagConstraints.WEST;
        gbc_chckbxFlow.gridx = 0;
        gbc_chckbxFlow.gridy = 2;
        chckbxFlow.addActionListener((e) -> {
            if (chckbxFlow.isSelected()) {
                comboBox_2.setEnabled(true);
            } else if (!chckbxFlow.isSelected()) {
                comboBox_2.setEnabled(false);
            }
        });
        panel.add(chckbxFlow, gbc_chckbxFlow);

        JComboBox<String> comboBox_3 = new JComboBox<>();
        comboBox_3.setEnabled(false);
        GridBagConstraints gbc_comboBox_3 = new GridBagConstraints();
        gbc_comboBox_3.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_3.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox_3.gridx = 9;
        gbc_comboBox_3.gridy = 3;
        String[] localVariables = {"Obfuscate", "Remove"};
        for (String s : localVariables) {
            comboBox_3.addItem(s);
        }
        panel.add(comboBox_3, gbc_comboBox_3);

        JCheckBox chckbxLocalVariables = new JCheckBox("Local Variables");
        GridBagConstraints gbc_chckbxNewCheckBox_2 = new GridBagConstraints();
        gbc_chckbxNewCheckBox_2.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxNewCheckBox_2.anchor = GridBagConstraints.WEST;
        gbc_chckbxNewCheckBox_2.gridx = 0;
        gbc_chckbxNewCheckBox_2.gridy = 3;
        chckbxLocalVariables.addActionListener((e) -> {
            if (chckbxLocalVariables.isSelected()) {
                comboBox_3.setEnabled(true);
            } else if (!chckbxLocalVariables.isSelected()) {
                comboBox_3.setEnabled(false);
            }
        });
        panel.add(chckbxLocalVariables, gbc_chckbxNewCheckBox_2);

        this.trashChanceField = new JTextField();
        this.trashChanceField.setToolTipText("Number of trash classes to " +
                "generate");
        this.trashChanceField.setEditable(false);
        this.trashChanceField.setText("10");
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.gridx = 9;
        gbc_textField.gridy = 7;
        panel.add(this.trashChanceField, gbc_textField);
        this.trashChanceField.setColumns(3);

        JCheckBox chckbxTrashClasses = new JCheckBox("Trash Classes");
        GridBagConstraints gbc_chckbxNewCheckBox_3 = new GridBagConstraints();
        gbc_chckbxNewCheckBox_3.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxNewCheckBox_3.anchor = GridBagConstraints.WEST;
        gbc_chckbxNewCheckBox_3.gridx = 0;
        gbc_chckbxNewCheckBox_3.gridy = 7;
        chckbxTrashClasses.addActionListener((e) -> {
            if (chckbxTrashClasses.isSelected()) {
                trashChanceField.setEditable(true);
            } else if (!chckbxTrashClasses.isSelected()) {
                trashChanceField.setEditable(false);
            }
        });
        panel.add(chckbxTrashClasses, gbc_chckbxNewCheckBox_3);

        JComboBox<String> comboBox_123 = new JComboBox<>();
        comboBox_123.setEnabled(false);
        GridBagConstraints gbc_comboBox_4 = new GridBagConstraints();
        gbc_comboBox_4.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_4.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox_4.gridx = 9;
        gbc_comboBox_4.gridy = 5;
        String[] sourceNameTypes = {"Obfuscate", "Remove"};
        for (String s : sourceNameTypes) {
            comboBox_123.addItem(s);
        }
        panel.add(comboBox_123, gbc_comboBox_4);

        JCheckBox chckbxSourceName = new JCheckBox("Source Name");
        GridBagConstraints gbc_chckbxNewCheckBox_123 = new GridBagConstraints();
        gbc_chckbxNewCheckBox_123.anchor = GridBagConstraints.WEST;
        gbc_chckbxNewCheckBox_123.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxNewCheckBox_123.gridx = 0;
        gbc_chckbxNewCheckBox_123.gridy = 5;
        chckbxSourceName.addActionListener((e) -> {
            if (chckbxSourceName.isSelected()) {
                comboBox_123.setEnabled(true);
            } else {
                comboBox_123.setEnabled(false);
            }
        });
        panel.add(chckbxSourceName, gbc_chckbxNewCheckBox_123);

        JComboBox<String> comboBox_1234 = new JComboBox<>();
        comboBox_1234.setEnabled(false);
        GridBagConstraints gbc_comboBox1234 = new GridBagConstraints();
        gbc_comboBox1234.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox1234.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox1234.gridx = 9;
        gbc_comboBox1234.gridy = 6;
        String[] sourceDebugTypes = {"Obfuscate", "Remove"};
        for (String s : sourceDebugTypes) {
            comboBox_1234.addItem(s);
        }
        panel.add(comboBox_1234, gbc_comboBox1234);

        JCheckBox chckbxSourceDebug = new JCheckBox("Source Debug");
        GridBagConstraints gbc_SourceDebug = new GridBagConstraints();
        gbc_SourceDebug.anchor = GridBagConstraints.WEST;
        gbc_SourceDebug.insets = new Insets(0, 0, 5, 5);
        gbc_SourceDebug.gridx = 0;
        gbc_SourceDebug.gridy = 6;
        chckbxSourceDebug.addActionListener((e) -> {
            if (chckbxSourceDebug.isSelected()) {
                comboBox_1234.setEnabled(true);
            } else {
                comboBox_1234.setEnabled(false);
            }
        });
        panel.add(chckbxSourceDebug, gbc_SourceDebug);

        JComboBox<String> comboBox_5 = new JComboBox<>();
        comboBox_5.setEnabled(false);
        GridBagConstraints gbc_comboBox_5 = new GridBagConstraints();
        gbc_comboBox_5.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_5.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox_5.gridx = 9;
        gbc_comboBox_5.gridy = 4;
        String[] lineTypes = {"Obfuscate", "Remove"};
        for (String s : lineTypes) {
            comboBox_5.addItem(s);
        }
        panel.add(comboBox_5, gbc_comboBox_5);

        JCheckBox chckbxLineObfuscation = new JCheckBox("Lines");
        GridBagConstraints gbc_chckbxNewCheckBox_10 = new GridBagConstraints();
        gbc_chckbxNewCheckBox_10.anchor = GridBagConstraints.WEST;
        gbc_chckbxNewCheckBox_10.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxNewCheckBox_10.gridx = 0;
        gbc_chckbxNewCheckBox_10.gridy = 4;
        chckbxLineObfuscation.addActionListener((e) -> {
            if (chckbxLineObfuscation.isSelected()) {
                comboBox_5.setEnabled(true);
            } else {
                comboBox_5.setEnabled(false);
            }
        });
        panel.add(chckbxLineObfuscation, gbc_chckbxNewCheckBox_10);

        JCheckBox chckbxSpringPool = new JCheckBox("String Pool");
        GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
        gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxNewCheckBox.anchor = GridBagConstraints.NORTHWEST;
        gbc_chckbxNewCheckBox.gridx = 0;
        gbc_chckbxNewCheckBox.gridy = 8;
        panel.add(chckbxSpringPool, gbc_chckbxNewCheckBox);

        JCheckBox chckbxCrasher = new JCheckBox("Crasher");
        GridBagConstraints gbc_chckbxAntidecompiler = new GridBagConstraints();
        gbc_chckbxAntidecompiler.anchor = GridBagConstraints.WEST;
        gbc_chckbxAntidecompiler.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxAntidecompiler.gridx = 0;
        gbc_chckbxAntidecompiler.gridy = 9;
        panel.add(chckbxCrasher, gbc_chckbxAntidecompiler);

        JCheckBox chckbxHidecode = new JCheckBox("Hide Code");
        GridBagConstraints gbc_chckbxNewCheckBox_4 = new GridBagConstraints();
        gbc_chckbxNewCheckBox_4.anchor = GridBagConstraints.WEST;
        gbc_chckbxNewCheckBox_4.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxNewCheckBox_4.gridx = 0;
        gbc_chckbxNewCheckBox_4.gridy = 10;
        panel.add(chckbxHidecode, gbc_chckbxNewCheckBox_4);

        JCheckBox chckbxNumberObfuscation
                = new JCheckBox("Number Obfuscation");
        GridBagConstraints gbc_chckbxNewCheckBox_9 = new GridBagConstraints();
        gbc_chckbxNewCheckBox_9.anchor = GridBagConstraints.WEST;
        gbc_chckbxNewCheckBox_9.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxNewCheckBox_9.gridx = 0;
        gbc_chckbxNewCheckBox_9.gridy = 11;
        panel.add(chckbxNumberObfuscation, gbc_chckbxNewCheckBox_9);

        JCheckBox chckbxSpigotPlugin = new JCheckBox("Spigot Plugin");
        chckbxSpigotPlugin.setToolTipText("Prevents string encryption from " +
                "encrypting strings that contain %%__USER__%% or " +
                "%%__RESOURCE__%% or %%__NONCE__%%.");
        GridBagConstraints gbc_chckbxNewCheckBox_7 = new GridBagConstraints();
        gbc_chckbxNewCheckBox_7.anchor = GridBagConstraints.WEST;
        gbc_chckbxNewCheckBox_7.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxNewCheckBox_7.gridx = 0;
        gbc_chckbxNewCheckBox_7.gridy = 12;
        panel.add(chckbxSpigotPlugin, gbc_chckbxNewCheckBox_7);

        JCheckBox chckbxClassRenammer = new JCheckBox("Member Renamer");
        GridBagConstraints gbc_chckbxClassRenammer = new GridBagConstraints();
        gbc_chckbxClassRenammer.anchor = GridBagConstraints.WEST;
        gbc_chckbxClassRenammer.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxClassRenammer.gridx = 0;
        gbc_chckbxClassRenammer.gridy = 13;
        panel.add(chckbxClassRenammer, gbc_chckbxClassRenammer);

        JCheckBox chckbxShuffler = new JCheckBox("Member Shuffler");
        GridBagConstraints gbc_chckbxShuffler = new GridBagConstraints();
        gbc_chckbxShuffler.anchor = GridBagConstraints.WEST;
        gbc_chckbxShuffler.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxShuffler.gridx = 0;
        gbc_chckbxShuffler.gridy = 14;
        panel.add(chckbxShuffler, gbc_chckbxShuffler);

        JCheckBox chckbxInnerClasses
                = new JCheckBox("InnerClasses Remover");
        GridBagConstraints gbc_chckbxInnerClasses = new GridBagConstraints();
        gbc_chckbxInnerClasses.anchor = GridBagConstraints.WEST;
        gbc_chckbxInnerClasses.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxInnerClasses.gridx = 0;
        gbc_chckbxInnerClasses.gridy = 15;
        panel.add(chckbxInnerClasses, gbc_chckbxInnerClasses);

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Watermark", null, panel_2, null);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                59, 0, 0};
        gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_panel_2.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        panel_2.setLayout(gbl_panel_2);

        JLabel lblNewLabel_1 = new JLabel("Message:");
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel_1.gridx = 0;
        gbc_lblNewLabel_1.gridy = 2;
        panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);

        this.waterMarkMessageField = new JTextField();
        this.waterMarkMessageField.setEnabled(false);
        GridBagConstraints gbc_textField_4 = new GridBagConstraints();
        gbc_textField_4.gridwidth = 11;
        gbc_textField_4.insets = new Insets(0, 0, 5, 5);
        gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_4.gridx = 1;
        gbc_textField_4.gridy = 2;
        panel_2.add(this.waterMarkMessageField, gbc_textField_4);
        this.waterMarkMessageField.setColumns(10);

        JLabel lblNewLabel_2 = new JLabel("Key:");
        GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
        gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel_2.gridx = 0;
        gbc_lblNewLabel_2.gridy = 3;
        panel_2.add(lblNewLabel_2, gbc_lblNewLabel_2);

        this.watermarkPassword = new JPasswordField();
        this.watermarkPassword.setEnabled(false);
        GridBagConstraints gbc_passwordField = new GridBagConstraints();
        gbc_passwordField.gridwidth = 11;
        gbc_passwordField.insets = new Insets(0, 0, 5, 5);
        gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
        gbc_passwordField.gridx = 1;
        gbc_passwordField.gridy = 3;
        panel_2.add(this.watermarkPassword, gbc_passwordField);

        JLabel lblNewLabel_3 = new JLabel("Embed Type:");
        GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
        gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel_3.gridx = 0;
        gbc_lblNewLabel_3.gridy = 4;
        panel_2.add(lblNewLabel_3, gbc_lblNewLabel_3);

        JComboBox<String> comboBox_05 = new JComboBox<>();
        comboBox_05.setEnabled(false);
        GridBagConstraints gbc_comboBox_05 = new GridBagConstraints();
        gbc_comboBox_05.anchor = GridBagConstraints.WEST;
        gbc_comboBox_05.gridwidth = 6;
        gbc_comboBox_05.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox_05.gridx = 1;
        gbc_comboBox_05.gridy = 4;
        String[] watermarks = {"Constant Pool", "Signature"};
        for (String s : watermarks) {
            comboBox_05.addItem(s);
        }
        panel_2.add(comboBox_05, gbc_comboBox_05);


        JCheckBox chckbxAddWatermark = new JCheckBox("Watermark");
        GridBagConstraints gbc_chckbxNewCheckBox_8 = new GridBagConstraints();
        gbc_chckbxNewCheckBox_8.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxNewCheckBox_8.gridx = 0;
        gbc_chckbxNewCheckBox_8.gridy = 0;
        chckbxAddWatermark.addActionListener((e) -> {
            if (chckbxAddWatermark.isSelected()) {
                watermarkPassword.setEnabled(true);
                waterMarkMessageField.setEnabled(true);
                comboBox_05.setEnabled(true);
            } else if (!chckbxAddWatermark.isSelected()) {
                watermarkPassword.setEnabled(false);
                waterMarkMessageField.setEnabled(false);
                comboBox_05.setEnabled(false);
            }
        });
        panel_2.add(chckbxAddWatermark, gbc_chckbxNewCheckBox_8);

        JSeparator separator_1 = new JSeparator();
        GridBagConstraints gbc_separator_1 = new GridBagConstraints();
        gbc_separator_1.insets = new Insets(0, 0, 5, 0);
        gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_separator_1.gridwidth = 13;
        gbc_separator_1.gridx = 0;
        gbc_separator_1.gridy = 5;
        panel_2.add(separator_1, gbc_separator_1);

        JLabel lblNewLabel = new JLabel("Extractor:");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 6;
        panel_2.add(lblNewLabel, gbc_lblNewLabel);

        JLabel lblNewLabel_4 = new JLabel("Input:");
        GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
        gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_4.gridx = 0;
        gbc_lblNewLabel_4.gridy = 8;
        panel_2.add(lblNewLabel_4, gbc_lblNewLabel_4);

        this.extractorInput = new JTextField();
        GridBagConstraints gbc_textField_5 = new GridBagConstraints();
        gbc_textField_5.gridwidth = 10;
        gbc_textField_5.insets = new Insets(0, 0, 5, 5);
        gbc_textField_5.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_5.gridx = 1;
        gbc_textField_5.gridy = 8;
        panel_2.add(this.extractorInput, gbc_textField_5);
        this.extractorInput.setColumns(10);

        JButton btnNewButton_4 = new JButton("Select");
        btnNewButton_4.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            if (extractorInput.getText() != null
                    && !extractorInput.getText().isEmpty()) {
                chooser.setSelectedFile(new File(extractorInput.getText()));
            }
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = chooser.showOpenDialog(frmRadonObfuscator);
            if (result == 0) {
                SwingUtilities.invokeLater(() ->
                        extractorInput.setText(chooser.getSelectedFile().getAbsolutePath())
                );
            }
        });
        GridBagConstraints gbc_btnNewButton_4 = new GridBagConstraints();
        gbc_btnNewButton_4.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton_4.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton_4.gridx = 11;
        gbc_btnNewButton_4.gridy = 8;
        panel_2.add(btnNewButton_4, gbc_btnNewButton_4);

        JLabel lblNewLabel_5 = new JLabel("Key:");
        GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
        gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_5.gridx = 0;
        gbc_lblNewLabel_5.gridy = 9;
        panel_2.add(lblNewLabel_5, gbc_lblNewLabel_5);

        this.extractorKey = new JPasswordField();
        GridBagConstraints gbc_passwordField_1 = new GridBagConstraints();
        gbc_passwordField_1.gridwidth = 10;
        gbc_passwordField_1.insets = new Insets(0, 0, 5, 5);
        gbc_passwordField_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_passwordField_1.gridx = 1;
        gbc_passwordField_1.gridy = 9;
        panel_2.add(this.extractorKey, gbc_passwordField_1);

        DefaultListModel<String> listModel = new DefaultListModel<>();

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 13;
        gbc_scrollPane.insets = new Insets(0, 4, 3, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 10;
        panel_2.add(scrollPane, gbc_scrollPane);

        JList<String> list_1 = new JList<>(listModel);
        scrollPane.setViewportView(list_1);

        JButton btnNewButton_5 = new JButton("Extract");
        GridBagConstraints gbc_btnNewButton_5 = new GridBagConstraints();
        gbc_btnNewButton_5.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton_5.gridx = 11;
        gbc_btnNewButton_5.gridy = 9;
        btnNewButton_5.addActionListener((e) -> {
            listModel.clear();

            if (extractorInput.getText() == null
                    || extractorInput.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No file specified!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (extractorKey.getPassword() == null || new String(extractorKey.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(null, "No key entered!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!extractorInput.getText().endsWith(".jar")) {
                JOptionPane.showMessageDialog(null,
                        "Only Jars are allowed!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                File input = new File(extractorInput.getText());
                List<String> foundIds =
                        WatermarkUtils.extractWatermark(input,
                                new String(extractorKey.getPassword()));
                for (String s : foundIds) {
                    listModel.addElement(s);
                }

                JOptionPane.showMessageDialog(null,
                        "Finished! Found: " +
                                String.valueOf(foundIds.size()) + " IDs",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
        panel_2.add(btnNewButton_5, gbc_btnNewButton_5);

        JPanel otherPanel = new JPanel();
        tabbedPane.addTab("Other", null, otherPanel, null);
        GridBagLayout gbl_otherPanel = new GridBagLayout();
        gbl_otherPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 59, 0, 0};
        gbl_otherPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0};
        gbl_otherPanel.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_otherPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        otherPanel.setLayout(gbl_otherPanel);

        JLabel expireMessageLabel = new JLabel("Message:");
        GridBagConstraints gbc_expireMessageLabel = new GridBagConstraints();
        gbc_expireMessageLabel.insets = new Insets(0, 0, 5, 5);
        gbc_expireMessageLabel.anchor = GridBagConstraints.EAST;
        gbc_expireMessageLabel.gridx = 0;
        gbc_expireMessageLabel.gridy = 2;
        otherPanel.add(expireMessageLabel, gbc_expireMessageLabel);

        this.expirationMessageField = new JTextField();
        this.expirationMessageField.setEnabled(false);
        GridBagConstraints gbc_messageField = new GridBagConstraints();
        gbc_messageField.gridwidth = 11;
        gbc_messageField.insets = new Insets(0, 0, 5, 5);
        gbc_messageField.fill = GridBagConstraints.HORIZONTAL;
        gbc_messageField.gridx = 1;
        gbc_messageField.gridy = 2;
        otherPanel.add(this.expirationMessageField, gbc_messageField);
        this.expirationMessageField.setColumns(10);

        JLabel expiresLabel = new JLabel("Expires:");
        GridBagConstraints gbc_expiresLabel = new GridBagConstraints();
        gbc_expiresLabel.insets = new Insets(0, 0, 5, 5);
        gbc_expiresLabel.anchor = GridBagConstraints.EAST;
        gbc_expiresLabel.gridx = 0;
        gbc_expiresLabel.gridy = 3;
        otherPanel.add(expiresLabel, gbc_expiresLabel);

        this.expirationDateField = new JTextField();
        this.expirationDateField.setEnabled(false);
        GridBagConstraints gbc_dateField = new GridBagConstraints();
        gbc_dateField.gridwidth = 11;
        gbc_dateField.insets = new Insets(0, 0, 5, 5);
        gbc_dateField.fill = GridBagConstraints.HORIZONTAL;
        gbc_dateField.gridx = 1;
        gbc_dateField.gridy = 3;
        otherPanel.add(this.expirationDateField, gbc_dateField);

        JCheckBox chckbxAddExpiration = new JCheckBox("Expiration");
        GridBagConstraints gbc_AddExpiration = new GridBagConstraints();
        gbc_AddExpiration.insets = new Insets(0, 0, 5, 5);
        gbc_AddExpiration.gridx = 0;
        gbc_AddExpiration.gridy = 0;
        chckbxAddExpiration.addActionListener((e) -> {
            if (chckbxAddExpiration.isSelected()) {
                expirationDateField.setEnabled(true);
                expirationMessageField.setEnabled(true);
            } else if (!chckbxAddExpiration.isSelected()) {
                expirationDateField.setEnabled(false);
                expirationMessageField.setEnabled(false);
            }
        });
        otherPanel.add(chckbxAddExpiration, gbc_AddExpiration);

        JSeparator expireSeparator = new JSeparator();
        GridBagConstraints gbc_expireSeparator = new GridBagConstraints();
        gbc_expireSeparator.insets = new Insets(0, 0, 5, 0);
        gbc_expireSeparator.fill = GridBagConstraints.HORIZONTAL;
        gbc_expireSeparator.gridwidth = 13;
        gbc_expireSeparator.gridx = 0;
        gbc_expireSeparator.gridy = 4;
        otherPanel.add(expireSeparator, gbc_expireSeparator);

        JComboBox<String> dictionaryComboBox = new JComboBox<>();
        dictionaryComboBox.setEnabled(true);
        GridBagConstraints gbc_dictionaryComboBox = new GridBagConstraints();
        gbc_dictionaryComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_dictionaryComboBox.insets = new Insets(0, 0, 5, 5);
        gbc_dictionaryComboBox.gridx = 11;
        gbc_dictionaryComboBox.gridy = 5;
        String[] dictionaries = {"0", "1", "2"};
        for (String s : dictionaries) {
            dictionaryComboBox.addItem(s);
        }
        otherPanel.add(dictionaryComboBox, gbc_dictionaryComboBox);

        JLabel chckbxDictionary = new JLabel("Dictionary:");
        GridBagConstraints gbc_chckbxDictionary = new GridBagConstraints();
        gbc_chckbxDictionary.anchor = GridBagConstraints.WEST;
        gbc_chckbxDictionary.insets = new Insets(0, 5, 5, 5);
        gbc_chckbxDictionary.gridx = 0;
        gbc_chckbxDictionary.gridy = 5;
        otherPanel.add(chckbxDictionary, gbc_chckbxDictionary);

        JButton garbageCollector = new JButton("GC");
        GridBagConstraints gbc_garbageCollector = new GridBagConstraints();
        gbc_garbageCollector.fill = GridBagConstraints.HORIZONTAL;
        gbc_garbageCollector.insets = new Insets(0, 0, 5, 5);
        gbc_garbageCollector.gridx = 11;
        gbc_garbageCollector.gridy = 6;
        garbageCollector.addActionListener((e) ->
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000) + "mb in use before garbage collector.");
                    System.gc();
                })
        );
        otherPanel.add(garbageCollector, gbc_garbageCollector);

        JPanel panel_3 = new JPanel();
        tabbedPane.addTab("Exempt", null, panel_3, null);
        GridBagLayout gbl_panel_3 = new GridBagLayout();
        gbl_panel_3.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0};
        gbl_panel_3.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0};
        gbl_panel_3.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel_3.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        panel_3.setLayout(gbl_panel_3);

        JComboBox<String> comboBox_04 = new JComboBox<>();
        GridBagConstraints gbc_comboBox_04 = new GridBagConstraints();
        gbc_comboBox_04.insets = new Insets(0, 5, 5, 5);
        gbc_comboBox_04.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_04.gridx = 1;
        gbc_comboBox_04.gridy = 12;
        String[] options = {"Class", "Method", "Field", "StringEncryption", "InvokeDynamic",
                "Flow", "LocalVars", "SourceName", "SourceDebug", "LineNumbers", "StringPool",
                "Crasher", "HideCode", "Numbers", "Shuffler", "InnerClasses", "Renamer",
                "Expiry"};
        for (String s : options) {
            comboBox_04.addItem(s);
        }

        JScrollPane scrollPane_1 = new JScrollPane();
        GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
        gbc_scrollPane_1.gridheight = 11;
        gbc_scrollPane_1.gridwidth = 11;
        gbc_scrollPane_1.insets = new Insets(0, 4, 5, 5);
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1.gridx = 1;
        gbc_scrollPane_1.gridy = 1;
        panel_3.add(scrollPane_1, gbc_scrollPane_1);

        DefaultListModel<String> exemptList = new DefaultListModel<>();

        JList<String> list = new JList<>(exemptList);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scrollPane_1.setViewportView(list);
        panel_3.add(comboBox_04, gbc_comboBox_04);

        this.exemptField = new JTextField();
        GridBagConstraints gbc_textField_3 = new GridBagConstraints();
        gbc_textField_3.gridwidth = 8;
        gbc_textField_3.insets = new Insets(0, 0, 5, 5);
        gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_3.gridx = 2;
        gbc_textField_3.gridy = 12;
        panel_3.add(this.exemptField, gbc_textField_3);
        this.exemptField.setColumns(10);

        JButton btnNewButton_2 = new JButton("Add");
        btnNewButton_2.addActionListener((e) -> {
            if (!exemptField.getText().equals("")
                    && exemptField.getText() != null
                    && !exemptField.getText().isEmpty()) {
                if (comboBox_04.getSelectedIndex() == 0) {
                    exemptList.addElement("Class: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 1) {
                    exemptList.addElement("Method: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 2) {
                    exemptList.addElement("Field: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 3) {
                    exemptList.addElement("StringEncryption: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 4) {
                    exemptList.addElement("InvokeDynamic: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 5) {
                    exemptList.addElement("Flow: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 6) {
                    exemptList.addElement("LocalVars: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 7) {
                    exemptList.addElement("SourceName: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 8) {
                    exemptList.addElement("SourceDebug: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 9) {
                    exemptList.addElement("LineNumbers: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 10) {
                    exemptList.addElement("StringPool: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 11) {
                    exemptList.addElement("Crasher: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 12) {
                    exemptList.addElement("HideCode: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 13) {
                    exemptList.addElement("Numbers: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 14) {
                    exemptList.addElement("Shuffler: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 15) {
                    exemptList.addElement("InnerClasses: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 16) {
                    exemptList.addElement("Renamer: " + exemptField.getText());
                    exemptField.setText("");
                } else if (comboBox_04.getSelectedIndex() == 17) {
                    exemptList.addElement("Expiry: " + exemptField.getText());
                    exemptField.setText("");
                }
            }
        });
        GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
        gbc_btnNewButton_2.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton_2.gridx = 10;
        gbc_btnNewButton_2.gridy = 12;
        panel_3.add(btnNewButton_2, gbc_btnNewButton_2);

        JButton btnNewButton_3 = new JButton("Remove");
        btnNewButton_3.addActionListener((e) -> {
            List<String> removeList = list.getSelectedValuesList();
            if (removeList.isEmpty())
                return;

            for (String s : removeList) {
                exemptList.removeElement(s);
            }
        });
        GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
        gbc_btnNewButton_3.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton_3.gridx = 11;
        gbc_btnNewButton_3.gridy = 12;
        panel_3.add(btnNewButton_3, gbc_btnNewButton_3);

        JPanel panel_7 = new JPanel();
        tabbedPane.addTab("About", null, panel_7, null);
        GridBagLayout gbl_panel_7 = new GridBagLayout();
        gbl_panel_7.columnWidths = new int[]{0, 0};
        gbl_panel_7.rowHeights = new int[]{0, 0};
        gbl_panel_7.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_panel_7.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        panel_7.setLayout(gbl_panel_7);

        JTextArea txtrPizzaObfuscatorAuthors = new JTextArea();
        txtrPizzaObfuscatorAuthors.setFont(new Font("Arial", Font.PLAIN, 12));
        txtrPizzaObfuscatorAuthors.setText(Radon.PREFIX + "\nVersion: " +
                Radon.VERSION + "\nAuthor: " + Radon.AUTHORS);
        txtrPizzaObfuscatorAuthors.setLineWrap(true);
        txtrPizzaObfuscatorAuthors.setEditable(false);
        GridBagConstraints gbc_txtrPizzaObfuscatorAuthors =
                new GridBagConstraints();
        gbc_txtrPizzaObfuscatorAuthors.fill = GridBagConstraints.BOTH;
        gbc_txtrPizzaObfuscatorAuthors.insets = new Insets(0, 2, 0, 0);
        gbc_txtrPizzaObfuscatorAuthors.gridx = 0;
        gbc_txtrPizzaObfuscatorAuthors.gridy = 0;
        panel_7.add(txtrPizzaObfuscatorAuthors, gbc_txtrPizzaObfuscatorAuthors);

        JPanel panel_1 = new JPanel();
        this.frmRadonObfuscator.getContentPane().add(panel_1, BorderLayout.SOUTH);
        panel_1.setLayout(new BorderLayout(0, 0));

        JPanel panel_5 = new JPanel();
        panel_1.add(panel_5, BorderLayout.EAST);

        JButton btnObfuscate = new JButton("    Process    ");
        btnObfuscate.addActionListener((e) -> {
            if (inputField.getText().isEmpty()
                    || inputField.getText() == null
                    || !inputField.getText().endsWith(".jar")) {
                JOptionPane.showMessageDialog(null,
                        "Invalid input JAR", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (outputField.getText().isEmpty()
                    || outputField.getText() == null
                    || !outputField.getText().endsWith(".jar")) {
                JOptionPane.showMessageDialog(null,
                        "Invalid output JAR", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (outputField.getText().equals(inputField.getText())) {
                JOptionPane.showMessageDialog(null,
                        "Output JAR can not have the same name as input " +
                                "JAR", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (chckbxAddWatermark.isSelected()
                    && waterMarkMessageField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "You must enter a message to be watermarked.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (chckbxAddWatermark.isSelected()
                    && new String(watermarkPassword.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "You must enter a key to encrypt the watermark message.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (chckbxAddExpiration.isSelected()
                    && expirationMessageField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "You must enter an expiration message.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (chckbxAddExpiration.isSelected()
                    && expirationDateField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "You must enter an expiration date.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            btnObfuscate.setText("Processing...");
            btnObfuscate.setEnabled(false);
            SwingWorker sw = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    File output = null;
                    try {

                        HashMap<String, File> libs = new HashMap<>();
                        for (int i = 0; i < libList.getSize(); i++) {
                            libs.put(libList.get(i), new File(libList.get(i)));
                        }

                        File input = new File(inputField.getText());
                        if (!input.exists()) {
                            JOptionPane.showMessageDialog(null,
                                    "Input JAR does not exist.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return null;
                        }
                        output = new File(outputField.getText());

                        int trashChance;
                        try {
                            trashChance =
                                    Integer.valueOf(trashChanceField.getText());
                        } catch (Throwable t) {
                            JOptionPane.showMessageDialog(null,
                                    "Please enter numbers only for the " +
                                            "number of desired trash " +
                                            "classes.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return null;
                        }
                        List<String> exempts = new ArrayList<>();
                        for (int i = 0; i < exemptList.size(); i++) {
                            exempts.add(exemptList.get(i));
                        }
                        boolean spigotMode = chckbxSpigotPlugin.isSelected();
                        List<AbstractTransformer> transformers
                                = new ArrayList<>();

                        if (chckbxClassRenammer.isSelected()) {
                            transformers.add(new Renamer(spigotMode));
                        }
                        if (chckbxInnerClasses.isSelected()) {
                            transformers.add(new InnerClassRemover());
                        }
                        if (chckbxNumberObfuscation.isSelected()) {
                            transformers.add(new NumberObfuscation());
                        }
                        if (chckbxInvokeDynamic.isSelected()) {
                            switch (comboBox_1.getSelectedIndex()) {
                                case 0:
                                    transformers.add(new LightInvokeDynamic());
                                    break;
                                case 1:
                                    transformers.add(new NormalInvokeDynamic());
                                    break;
                                case 2:
                                    transformers.add(new HeavyInvokeDynamic());
                                    break;
                            }
                        }
                        if (chckbxAddExpiration.isSelected()) {
                            if (!expirationDateField.getText().isEmpty()
                                    && !expirationMessageField.getText()
                                    .isEmpty()) {
                                long expireTime =
                                        new SimpleDateFormat("MM/dd/yyyy")
                                                .parse(expirationDateField
                                                        .getText()).getTime();
                                transformers.add(new Expiry(expireTime,
                                        expirationMessageField.getText()));
                            }
                        }
                        if (chckbxStringEncryption.isSelected()) {
                            switch (comboBox.getSelectedIndex()) {
                                case 0:
                                    transformers.add(new VeryLightStringEncryption(spigotMode));
                                    break;
                                case 1:
                                    transformers.add(new LightStringEncryption(spigotMode));
                                    break;
                                case 2:
                                    transformers.add(new NormalStringEncryption(spigotMode));
                                    break;
                                case 3:
                                    transformers.add(new HeavyStringEncryption(spigotMode));
                                    break;
                            }
                        }
                        if (chckbxFlow.isSelected()) {
                            switch (comboBox_2.getSelectedIndex()) {
                                case 0:
                                    transformers.add(new LightFlowObfuscation());
                                    break;
                                case 1:
                                    transformers.add(new NormalFlowObfuscation());
                                    break;
                                case 2:
                                    transformers.add(new HeavyFlowObfuscation());
                                    break;
                            }
                        }
                        if (chckbxSpringPool.isSelected()) {
                            transformers.add(new StringPool());
                        }
                        if (chckbxShuffler.isSelected()) {
                            transformers.add(new Shuffler());
                        }
                        if (chckbxLocalVariables.isSelected()) {
                            switch (comboBox_3.getSelectedIndex()) {
                                case 0:
                                    transformers.add(new ObfuscateLocalVariables());
                                    break;
                                case 1:
                                    transformers.add(new RemoveLocalVariables());
                                    break;
                            }
                        }
                        if (chckbxLineObfuscation.isSelected()) {
                            switch (comboBox_5.getSelectedIndex()) {
                                case 0:
                                    transformers.add(new ObfuscateLineNumbers());
                                    break;
                                case 1:
                                    transformers.add(new RemoveLineNumbers());
                                    break;
                            }
                        }
                        if (chckbxSourceName.isSelected()) {
                            switch (comboBox_123.getSelectedIndex()) {
                                case 0:
                                    transformers.add(new ObfuscateSourceName());
                                    break;
                                case 1:
                                    transformers.add(new RemoveSourceName());
                                    break;
                            }
                        }
                        if (chckbxSourceDebug.isSelected()) {
                            switch (comboBox_1234.getSelectedIndex()) {
                                case 0:
                                    transformers.add(new ObfuscateSourceDebug());
                                    break;
                                case 1:
                                    transformers.add(new RemoveSourceDebug());
                                    break;
                            }
                        }
                        if (chckbxCrasher.isSelected()) {
                            transformers.add(new Crasher());
                        }
                        if (chckbxHidecode.isSelected()) {
                            transformers.add(new HideCode());
                        }

                        int trashClasses = -1;
                        if (chckbxTrashClasses.isSelected()) {
                            trashClasses = trashChance;
                        }

                        int watermarkType = -1;
                        if (chckbxAddWatermark.isSelected()) {
                            watermarkType = comboBox_05.getSelectedIndex();
                        }

                        int dictionary = dictionaryComboBox.getSelectedIndex();

                        if (chckbxTrashClasses.isSelected() && spigotMode) {
                            throw new RuntimeException("Trash classes are not compatible with Spigot's anti-piracy injection.");
                        }

                        new ConsoleGUI();
                        Bootstrap bootstrap = new Bootstrap(
                                input,
                                output,
                                libs,
                                exempts,
                                transformers,
                                trashClasses,
                                waterMarkMessageField.getText(),
                                watermarkType,
                                new String(watermarkPassword.getPassword()),
                                dictionary);
                        bootstrap.startTheParty(false);
                        JOptionPane.showMessageDialog(null,
                                "Successfully processed file!",
                                "Done", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Throwable t) {
                        JOptionPane.showMessageDialog(null, t.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        t.printStackTrace();
                        if (output != null) {
                            if (!output.delete()) {
                                JOptionPane.showMessageDialog(null, "Failed to delete fault output.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } finally {
                        btnObfuscate.setEnabled(true);
                        btnObfuscate.setText("    Process    ");
                    }
                    return null;
                }
            };

            sw.execute();
        });
        panel_5.add(btnObfuscate);

        JPanel panel_6 = new JPanel();
        panel_1.add(panel_6, BorderLayout.WEST);

        JButton btnLoadConfiguration = new JButton("Load Configuration");
        btnLoadConfiguration.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = chooser.showOpenDialog(frmRadonObfuscator);
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        File config = new File(chooser.getSelectedFile()
                                .getAbsolutePath());
                        if (!config.exists())
                            throw new IOException("Config file does " +
                                    "not exist.");

                        FileInputStream fis = new FileInputStream(config);
                        Config configParser
                                = new Config(fis);
                        configParser.loadIntoMap();
                        configParser.sortExempts();
                        configParser.checkConfig();

                        inputField.setText(configParser.getInput()
                                .getAbsolutePath());
                        outputField.setText(configParser.getOutput()
                                .getAbsolutePath());
                        libList.clear();
                        for (String s : configParser.getLibraries()
                                .keySet()) {
                            libList.addElement(s);
                        }

                        List<String> exempts = configParser.getExempts();
                        if (exempts != null) {
                            exemptList.clear();
                            for (String s : exempts) {
                                exemptList.addElement(s);
                            }
                        }

                        AbstractTransformer stringEncryptionMode
                                = configParser.getStringEncryptionType();
                        if (stringEncryptionMode == null) {
                            chckbxStringEncryption.setSelected(false);
                        } else if (stringEncryptionMode
                                instanceof VeryLightStringEncryption) {
                            chckbxStringEncryption.setSelected(true);
                            comboBox.setSelectedIndex(0);
                            comboBox.setEnabled(true);
                        } else if (stringEncryptionMode
                                instanceof LightStringEncryption) {
                            chckbxStringEncryption.setSelected(true);
                            comboBox.setSelectedIndex(1);
                            comboBox.setEnabled(true);
                        } else if (stringEncryptionMode
                                instanceof NormalStringEncryption) {
                            chckbxStringEncryption.setSelected(true);
                            comboBox.setSelectedIndex(2);
                            comboBox.setEnabled(true);
                        } else if (stringEncryptionMode
                                instanceof HeavyStringEncryption) {
                            chckbxStringEncryption.setSelected(true);
                            comboBox.setSelectedIndex(3);
                            comboBox.setEnabled(true);
                        }

                        AbstractTransformer flowObfuscationMode =
                                configParser.getFlowObfuscationType();
                        if (flowObfuscationMode == null) {
                            chckbxFlow.setSelected(false);
                        } else if (flowObfuscationMode
                                instanceof LightFlowObfuscation) {
                            chckbxFlow.setSelected(true);
                            comboBox_2.setSelectedIndex(0);
                            comboBox_2.setEnabled(true);
                        } else if (flowObfuscationMode
                                instanceof NormalFlowObfuscation) {
                            chckbxFlow.setSelected(true);
                            comboBox_2.setSelectedIndex(1);
                            comboBox_2.setEnabled(true);
                        } else if (flowObfuscationMode
                                instanceof HeavyFlowObfuscation) {
                            chckbxFlow.setSelected(true);
                            comboBox_2.setSelectedIndex(2);
                            comboBox_2.setEnabled(true);
                        }

                        AbstractTransformer invokeDynamicMode =
                                configParser.getInvokeDynamicType();
                        if (invokeDynamicMode == null) {
                            chckbxInvokeDynamic.setSelected(false);
                        } else if (invokeDynamicMode
                                instanceof LightInvokeDynamic) {
                            chckbxInvokeDynamic.setSelected(true);
                            comboBox_1.setSelectedIndex(0);
                            comboBox_1.setEnabled(true);
                        } else if (invokeDynamicMode
                                instanceof NormalInvokeDynamic) {
                            chckbxInvokeDynamic.setSelected(true);
                            comboBox_1.setSelectedIndex(1);
                            comboBox_1.setEnabled(true);
                        } else if (invokeDynamicMode
                                instanceof HeavyInvokeDynamic) {
                            chckbxInvokeDynamic.setSelected(true);
                            comboBox_1.setSelectedIndex(2);
                            comboBox_1.setEnabled(true);
                        }

                        AbstractTransformer localVariablesMode =
                                configParser.getLocalVariableObfuscationType();
                        if (localVariablesMode == null) {
                            chckbxLocalVariables.setSelected(false);
                        } else if (localVariablesMode
                                instanceof ObfuscateLocalVariables) {
                            chckbxLocalVariables.setSelected(true);
                            comboBox_3.setSelectedIndex(0);
                            comboBox_3.setEnabled(true);
                        } else if (localVariablesMode
                                instanceof RemoveLocalVariables) {
                            chckbxLocalVariables.setSelected(true);
                            comboBox_3.setSelectedIndex(1);
                            comboBox_3.setEnabled(true);
                        }

                        AbstractTransformer crasherMode =
                                configParser.getCrasherType();
                        if (crasherMode instanceof Crasher) {
                            chckbxCrasher.setSelected(true);
                        } else {
                            chckbxCrasher.setSelected(false);
                        }

                        AbstractTransformer hideCodeMode =
                                configParser.getHideCodeType();
                        if (hideCodeMode instanceof HideCode) {
                            chckbxHidecode.setSelected(true);
                        } else {
                            chckbxHidecode.setSelected(false);
                        }

                        AbstractTransformer lineRemoverMode =
                                configParser.getLineNumberObfuscationType();
                        if (lineRemoverMode == null) {
                            chckbxLineObfuscation.setSelected(false);
                        } else if (lineRemoverMode
                                instanceof ObfuscateLineNumbers) {
                            chckbxLineObfuscation.setSelected(true);
                            comboBox_5.setSelectedIndex(0);
                            comboBox_5.setEnabled(true);
                        } else if (lineRemoverMode
                                instanceof RemoveLineNumbers) {
                            chckbxLineObfuscation.setSelected(true);
                            comboBox_5.setSelectedIndex(1);
                            comboBox_5.setEnabled(true);
                        }

                        AbstractTransformer numberObfuscationMode =
                                configParser.getNumberObfuscationType();
                        if (numberObfuscationMode
                                instanceof NumberObfuscation) {
                            chckbxNumberObfuscation.setSelected(true);
                        } else {
                            chckbxNumberObfuscation.setSelected(false);
                        }

                        AbstractTransformer sourceNameObfuscationMode =
                                configParser.getSourceNameObfuscationType();
                        if (sourceNameObfuscationMode == null) {
                            chckbxSourceName.setSelected(false);
                        } else if (sourceNameObfuscationMode
                                instanceof ObfuscateSourceName) {
                            chckbxSourceName.setSelected(true);
                            comboBox_123.setSelectedIndex(0);
                            comboBox_123.setEnabled(true);
                        } else if (sourceNameObfuscationMode
                                instanceof RemoveSourceName) {
                            chckbxSourceName.setSelected(true);
                            comboBox_123.setSelectedIndex(1);
                            comboBox_123.setEnabled(true);
                        }

                        AbstractTransformer sourceDebugObfuscationMode =
                                configParser.getSourceDebugObfuscationType();
                        if (sourceDebugObfuscationMode == null) {
                            chckbxSourceDebug.setSelected(false);
                        } else if (sourceDebugObfuscationMode
                                instanceof ObfuscateSourceDebug) {
                            chckbxSourceDebug.setSelected(true);
                            comboBox_1234.setSelectedIndex(0);
                            comboBox_1234.setEnabled(true);
                        } else if (sourceDebugObfuscationMode
                                instanceof RemoveSourceDebug) {
                            chckbxSourceDebug.setSelected(true);
                            comboBox_1234.setSelectedIndex(1);
                            comboBox_1234.setEnabled(true);
                        }


                        AbstractTransformer stringPoolMode =
                                configParser.getStringPoolType();
                        if (stringPoolMode instanceof StringPool) {
                            chckbxSpringPool.setSelected(true);
                        } else {
                            chckbxSpringPool.setSelected(false);
                        }

                        AbstractTransformer shufflerMode =
                                configParser.getShufflerType();
                        if (shufflerMode instanceof Shuffler) {
                            chckbxShuffler.setSelected(true);
                        } else {
                            chckbxShuffler.setSelected(false);
                        }

                        AbstractTransformer innerClassMode =
                                configParser.getInnerClassRemoverType();
                        if (innerClassMode instanceof InnerClassRemover) {
                            chckbxInnerClasses.setSelected(true);
                        } else {
                            chckbxInnerClasses.setSelected(false);
                        }

                        AbstractTransformer renamer =
                                configParser.getRenamerType();
                        if (renamer instanceof Renamer) {
                            chckbxClassRenammer.setSelected(true);
                        } else {
                            chckbxClassRenammer.setSelected(false);
                        }

                        if (configParser.getSpigotBool()) {
                            chckbxSpigotPlugin.setSelected(true);
                        } else {
                            chckbxSpigotPlugin.setSelected(false);
                        }

                        int trashClassesChance =
                                configParser.getTrashClasses();
                        if (trashClassesChance == -1) {
                            chckbxTrashClasses.setSelected(false);
                        } else {
                            chckbxTrashClasses.setSelected(true);
                            trashChanceField.setText(String
                                    .valueOf(trashClassesChance));
                            trashChanceField.setEditable(true);
                        }

                        waterMarkMessageField.setText(configParser
                                .getWatermarkMsg());
                        watermarkPassword.setText(configParser
                                .getWatermarkKey());

                        int watermarkType =
                                configParser.getWatermarkType();
                        if (configParser.getWatermarkType() != -1) {
                            chckbxAddWatermark.setSelected(true);
                            waterMarkMessageField.setEnabled(true);
                            watermarkPassword.setEnabled(true);
                            comboBox_05.setEnabled(true);
                            comboBox_05.setSelectedIndex(watermarkType);
                        }

                        if (configParser.getExpiryMsg() != null
                                && configParser.getExpiryTime() != -1) {
                            expirationMessageField.setText(configParser
                                    .getExpiryMsg());
                            expirationMessageField.setEnabled(true);
                            expirationDateField.setText(String
                                    .valueOf(new SimpleDateFormat("MM/dd/yyyy")
                                            .format(configParser.getExpiryTime())));
                            expirationDateField.setEnabled(true);
                            chckbxAddExpiration.setSelected(true);
                        }

                        int dictionary = configParser.getDictionaryType();
                        dictionaryComboBox.setSelectedIndex(dictionary);
                        fis.close();

                        lastPath = chooser.getSelectedFile();
                    } catch (Throwable t) {
                        JOptionPane.showMessageDialog(null,
                                t.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        t.printStackTrace();
                    }
                });
            }
        });
        btnLoadConfiguration.setToolTipText("Loads config for pre-defined " +
                "settings.");
        panel_6.add(btnLoadConfiguration);
        JButton saveConfigButton = new JButton("Save configuration");
        saveConfigButton.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = chooser.showOpenDialog(frmRadonObfuscator);
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        Map<ConfigEnum, Object> settings = new HashMap<>();
                        if (inputField.getText() != null
                                && !inputField.getText().isEmpty()) {
                            settings.put(ConfigEnum.INPUT, inputField.getText());
                        }

                        if (outputField.getText() != null
                                && !outputField.getText().isEmpty()) {
                            settings.put(ConfigEnum.OUTPUT, outputField.getText());
                        }

                        if (chckbxStringEncryption.isSelected()) {
                            switch (comboBox.getSelectedIndex()) {
                                case 0:
                                    settings.put(ConfigEnum.STRING_ENCRYPTION, "SuperLight");
                                    break;
                                case 1:
                                    settings.put(ConfigEnum.STRING_ENCRYPTION, "Light");
                                    break;
                                case 2:
                                    settings.put(ConfigEnum.STRING_ENCRYPTION, "Normal");
                                    break;
                                case 3:
                                    settings.put(ConfigEnum.STRING_ENCRYPTION, "Heavy");
                                    break;
                            }
                        }

                        if (chckbxInvokeDynamic.isSelected()) {
                            switch (comboBox_1.getSelectedIndex()) {
                                case 0:
                                    settings.put(ConfigEnum.INVOKEDYNAMIC, "Light");
                                    break;
                                case 1:
                                    settings.put(ConfigEnum.INVOKEDYNAMIC, "Normal");
                                    break;
                                case 2:
                                    settings.put(ConfigEnum.INVOKEDYNAMIC, "Heavy");
                                    break;
                            }
                        }

                        if (chckbxFlow.isSelected()) {
                            switch (comboBox_2.getSelectedIndex()) {
                                case 0:
                                    settings.put(ConfigEnum.FLOW_OBFUSCATION, "Light");
                                    break;
                                case 1:
                                    settings.put(ConfigEnum.FLOW_OBFUSCATION, "Normal");
                                    break;
                                case 2:
                                    settings.put(ConfigEnum.FLOW_OBFUSCATION, "Heavy");
                                    break;
                            }
                        }

                        if (chckbxLocalVariables.isSelected()) {
                            switch (comboBox_3.getSelectedIndex()) {
                                case 0:
                                    settings.put(ConfigEnum.LOCAL_VARIABLES, "Obfuscate");
                                    break;
                                case 1:
                                    settings.put(ConfigEnum.LOCAL_VARIABLES, "Remove");
                                    break;
                            }
                        }

                        if (chckbxCrasher.isSelected()) {
                            settings.put(ConfigEnum.CRASHER, "true");
                        }

                        if (chckbxHidecode.isSelected()) {
                            settings.put(ConfigEnum.HIDER, "true");
                        }

                        if (chckbxSpringPool.isSelected()) {
                            settings.put(ConfigEnum.STRING_POOL, "true");
                        }

                        if (chckbxLineObfuscation.isSelected()) {
                            switch (comboBox_5.getSelectedIndex()) {
                                case 0:
                                    settings.put(ConfigEnum.LINE_NUMBERS, "Obfuscate");
                                    break;
                                case 1:
                                    settings.put(ConfigEnum.LINE_NUMBERS, "Remove");
                                    break;
                            }
                        }

                        if (chckbxNumberObfuscation.isSelected()) {
                            settings.put(ConfigEnum.NUMBERS, "true");
                        }

                        if (chckbxSourceName.isSelected()) {
                            switch (comboBox_123.getSelectedIndex()) {
                                case 0:
                                    settings.put(ConfigEnum.SOURCE_NAME, "Obfuscate");
                                    break;
                                case 1:
                                    settings.put(ConfigEnum.SOURCE_NAME, "Remove");
                                    break;
                            }
                        }

                        if (chckbxSourceDebug.isSelected()) {
                            switch (comboBox_1234.getSelectedIndex()) {
                                case 0:
                                    settings.put(ConfigEnum.SOURCE_DEBUG, "Obfuscate");
                                    break;
                                case 1:
                                    settings.put(ConfigEnum.SOURCE_DEBUG, "Remove");
                                    break;
                            }
                        }

                        if (chckbxTrashClasses.isSelected()) {
                            if (trashChanceField.getText() != null
                                    && !trashChanceField.getText().isEmpty()) {
                                settings.put(ConfigEnum.TRASH_CLASSES, trashChanceField.getText());
                            }
                        }

                        if (chckbxAddWatermark.isSelected()) {
                            if (waterMarkMessageField.getText() != null
                                    && !waterMarkMessageField.getText().isEmpty()
                                    && watermarkPassword.getPassword() != null
                                    && !new String(watermarkPassword.getPassword()).isEmpty()) {
                                switch (comboBox_05.getSelectedIndex()) {
                                    case 0:
                                        settings.put(ConfigEnum.WATERMARK_TYPE, "ConstantPool");
                                        break;
                                    case 1:
                                        settings.put(ConfigEnum.WATERMARK_TYPE, "Signature");
                                        break;
                                }

                                settings.put(ConfigEnum.WATERMARK_MSG, waterMarkMessageField.getText());
                                settings.put(ConfigEnum.WATERMARK_KEY, new String(watermarkPassword.getPassword()));
                            }
                        }

                        if (chckbxSpigotPlugin.isSelected()) {
                            settings.put(ConfigEnum.SPIGOT_PLUGIN, "true");
                        }

                        if (chckbxClassRenammer.isSelected()) {
                            settings.put(ConfigEnum.RENAMER, "true");
                        }

                        if (chckbxAddExpiration.isSelected()) {
                            if (expirationMessageField.getText() != null
                                    && !expireMessageLabel.getText().isEmpty()
                                    && expirationDateField.getText() != null
                                    && !expirationDateField.getText().isEmpty()) {
                                settings.put(ConfigEnum.EXPIRATION_MESSAGE, expirationMessageField.getText());
                                settings.put(ConfigEnum.EXPIRATION_TIME, expirationDateField.getText());
                            }
                        }

                        if (chckbxShuffler.isSelected()) {
                            settings.put(ConfigEnum.SHUFFLER, "true");
                        }

                        settings.put(ConfigEnum.DICTIONARY, String.valueOf(dictionaryComboBox.getSelectedIndex()));

                        if (chckbxInnerClasses.isSelected()) {
                            settings.put(ConfigEnum.INNERCLASSES, "true");
                        }

                        List<String> libs = new ArrayList<>();
                        for (int i = 0; i < libList.size(); i++) {
                            String lib = libList.get(i);
                            libs.add(lib);
                        }

                        settings.put(ConfigEnum.LIBRARIES, libs);

                        List<String> exempts = new ArrayList<>();
                        for (int i = 0; i < exemptList.size(); i++) {
                            String exempt = exemptList.get(i);
                            exempts.add(exempt);
                        }

                        settings.put(ConfigEnum.EXEMPTS, exempts);

                        ConfigWriter writer = new ConfigWriter(settings);
                        writer.parseOptions();
                        writer.writeConfig(chooser.getSelectedFile().getAbsolutePath());

                        lastPath = chooser.getSelectedFile();
                    } catch (Throwable t) {
                        JOptionPane.showMessageDialog(null,
                                t.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        t.printStackTrace();
                    }
                });
            }
        });
        panel_6.add(saveConfigButton);
        this.frmRadonObfuscator.setVisible(true);
    }
}
