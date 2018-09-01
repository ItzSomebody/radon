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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import me.itzsomebody.radon.SessionInfo;
import me.itzsomebody.radon.gui.StringEncryptionExclusionGUI;
import me.itzsomebody.radon.transformers.miscellaneous.Crasher;
import me.itzsomebody.radon.transformers.obfuscators.flow.FlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.flow.HeavyFlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.flow.LightFlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.flow.NormalFlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.invokedynamic.HeavyInvokeDynamic;
import me.itzsomebody.radon.transformers.obfuscators.invokedynamic.InvokeDynamic;
import me.itzsomebody.radon.transformers.obfuscators.invokedynamic.LightInvokeDynamic;
import me.itzsomebody.radon.transformers.obfuscators.invokedynamic.NormalInvokeDynamic;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.HideCode;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.LineNumbers;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.LocalVariables;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.MemberShuffler;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.SourceDebug;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.SourceName;
import me.itzsomebody.radon.transformers.obfuscators.numbers.HeavyNumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.numbers.LightNumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.numbers.NormalNumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.numbers.NumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.renamer.Renamer;
import me.itzsomebody.radon.transformers.obfuscators.renamer.RenamerSetup;
import me.itzsomebody.radon.transformers.obfuscators.strings.HeavyStringEncryption;
import me.itzsomebody.radon.transformers.obfuscators.strings.LightStringEncryption;
import me.itzsomebody.radon.transformers.obfuscators.strings.NormalStringEncryption;
import me.itzsomebody.radon.transformers.obfuscators.strings.StringEncryption;
import me.itzsomebody.radon.transformers.obfuscators.strings.StringEncryptionSetup;
import me.itzsomebody.radon.transformers.obfuscators.strings.StringPool;

public class ObfuscationTab extends JPanel {
    public static ArrayList<String> stringExclusions = new ArrayList<>();
    private JComboBox<String> stringEncryptionTypeSelector;
    private JCheckBox stringPoolCheckBox;
    private JCheckBox stringEncryptionEnabledCheckBox;
    private JButton stringEncryptionExclusionButtons;

    private JCheckBox renamingRepackageCheckBox;
    private JTextField renamingRepackageField;
    private JCheckBox renamingAdaptResources;
    private JTextField renamingResourcesField;
    private JCheckBox renamingEnabledCheckBox;

    private JComboBox<String> invokeDynamicComboBox;
    private JCheckBox invokeDynamicCheckBox;

    private JComboBox<String> flowComboBox;
    private JCheckBox flowCheckBox;

    private JComboBox<String> numberObfuscationComboBox;
    private JCheckBox numberObfuscationCheckBox;

    private JCheckBox localVarsRemove;
    private JCheckBox localVarCheckBox;

    private JCheckBox lineNumbersRemove;
    private JCheckBox lineNumbersCheckBox;

    private JCheckBox sourceNameRemove;
    private JCheckBox sourceNameCheckBox;

    private JCheckBox sourceDebugRemove;
    private JCheckBox sourceDebugCheckBox;

    private JCheckBox hideCodeCheckBox;
    private JCheckBox shufflerCheckBox;
    private JCheckBox crasherCheckBox;

    public ObfuscationTab() {
        GridBagLayout gbl_this = new GridBagLayout();
        gbl_this.columnWidths = new int[]{0, 0};
        gbl_this.rowHeights = new int[]{35, 25, 162, 0};
        gbl_this.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_this.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        this.setLayout(gbl_this);

        JPanel stringEncryptionPanel = new JPanel();
        GridBagConstraints gbc_stringEncryptionPanel = new GridBagConstraints();
        gbc_stringEncryptionPanel.insets = new Insets(0, 0, 5, 0);
        gbc_stringEncryptionPanel.fill = GridBagConstraints.BOTH;
        gbc_stringEncryptionPanel.gridx = 0;
        gbc_stringEncryptionPanel.gridy = 0;
        this.add(stringEncryptionPanel, gbc_stringEncryptionPanel);
        GridBagLayout gbl_stringEncryptionPanel = new GridBagLayout();
        gbl_stringEncryptionPanel.columnWidths = new int[]{0, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_stringEncryptionPanel.rowHeights = new int[]{0, 0, 0};
        gbl_stringEncryptionPanel.columnWeights = new double[]{1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_stringEncryptionPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        stringEncryptionPanel.setBorder(new TitledBorder("String Encryption"));
        stringEncryptionPanel.setLayout(gbl_stringEncryptionPanel);

        stringEncryptionTypeSelector = new JComboBox<>();
        GridBagConstraints gbc_stringEncryptionTypeSelector = new GridBagConstraints();
        gbc_stringEncryptionTypeSelector.fill = GridBagConstraints.HORIZONTAL;
        gbc_stringEncryptionTypeSelector.insets = new Insets(0, 0, 5, 5);
        gbc_stringEncryptionTypeSelector.gridx = 15;
        gbc_stringEncryptionTypeSelector.gridy = 0;
        stringEncryptionTypeSelector.addItem("Light");
        stringEncryptionTypeSelector.addItem("Normal");
        stringEncryptionTypeSelector.addItem("Heavy");
        stringEncryptionTypeSelector.setEnabled(false);
        stringEncryptionPanel.add(stringEncryptionTypeSelector, gbc_stringEncryptionTypeSelector);

        stringPoolCheckBox = new JCheckBox("Pool Strings");
        GridBagConstraints gbc_stringPoolCheckBox = new GridBagConstraints();
        gbc_stringPoolCheckBox.anchor = GridBagConstraints.WEST;
        gbc_stringPoolCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_stringPoolCheckBox.gridx = 0;
        gbc_stringPoolCheckBox.gridy = 1;
        stringPoolCheckBox.setEnabled(false);
        stringEncryptionPanel.add(stringPoolCheckBox, gbc_stringPoolCheckBox);

        stringEncryptionExclusionButtons = new JButton("Exclusions");
        GridBagConstraints gbc_stringEncryptionExclusionButtons = new GridBagConstraints();
        gbc_stringEncryptionExclusionButtons.anchor = GridBagConstraints.EAST;
        gbc_stringEncryptionExclusionButtons.insets = new Insets(0, 0, 5, 5);
        gbc_stringEncryptionExclusionButtons.gridx = 15;
        gbc_stringEncryptionExclusionButtons.gridy = 1;
        stringEncryptionExclusionButtons.setEnabled(false);
        stringEncryptionExclusionButtons.addActionListener((e) -> new StringEncryptionExclusionGUI());
        stringEncryptionPanel.add(stringEncryptionExclusionButtons, gbc_stringEncryptionExclusionButtons);

        stringEncryptionEnabledCheckBox = new JCheckBox("Enabled");
        GridBagConstraints gbc_stringEncryptionEnabledCheckBox = new GridBagConstraints();
        gbc_stringEncryptionEnabledCheckBox.anchor = GridBagConstraints.WEST;
        gbc_stringEncryptionEnabledCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_stringEncryptionEnabledCheckBox.gridx = 0;
        gbc_stringEncryptionEnabledCheckBox.gridy = 0;
        stringEncryptionEnabledCheckBox.addActionListener((e) -> {
            boolean enable = stringEncryptionEnabledCheckBox.isSelected();
            stringEncryptionTypeSelector.setEnabled(enable);
            stringPoolCheckBox.setEnabled(enable);
            stringEncryptionExclusionButtons.setEnabled(enable);
        });
        stringEncryptionPanel.add(stringEncryptionEnabledCheckBox, gbc_stringEncryptionEnabledCheckBox);

        JPanel renamingPanel = new JPanel();
        GridBagConstraints gbc_renamingPanel = new GridBagConstraints();
        gbc_renamingPanel.insets = new Insets(0, 0, 5, 0);
        gbc_renamingPanel.fill = GridBagConstraints.BOTH;
        gbc_renamingPanel.gridx = 0;
        gbc_renamingPanel.gridy = 1;
        renamingPanel.setBorder(new TitledBorder("Renaming"));
        this.add(renamingPanel, gbc_renamingPanel);
        GridBagLayout gbl_renamingPanel = new GridBagLayout();
        gbl_renamingPanel.columnWidths = new int[]{0, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_renamingPanel.rowHeights = new int[]{0, 0, 0, 0};
        gbl_renamingPanel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_renamingPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        renamingPanel.setLayout(gbl_renamingPanel);

        renamingRepackageCheckBox = new JCheckBox("Repackage");
        GridBagConstraints gbc_renamingRepackageCheckBox = new GridBagConstraints();
        gbc_renamingRepackageCheckBox.anchor = GridBagConstraints.WEST;
        gbc_renamingRepackageCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_renamingRepackageCheckBox.gridx = 0;
        gbc_renamingRepackageCheckBox.gridy = 1;
        renamingRepackageCheckBox.setEnabled(false);
        renamingPanel.add(renamingRepackageCheckBox, gbc_renamingRepackageCheckBox);

        renamingRepackageField = new JTextField();
        GridBagConstraints gbc_renamingRepackageField = new GridBagConstraints();
        gbc_renamingRepackageField.gridwidth = 2;
        gbc_renamingRepackageField.insets = new Insets(0, 0, 5, 5);
        gbc_renamingRepackageField.fill = GridBagConstraints.HORIZONTAL;
        gbc_renamingRepackageField.gridx = 14;
        gbc_renamingRepackageField.gridy = 1;
        renamingRepackageField.setEditable(false);
        renamingPanel.add(renamingRepackageField, gbc_renamingRepackageField);
        renamingRepackageField.setColumns(10);

        renamingAdaptResources = new JCheckBox("Adapt Resources");
        GridBagConstraints gbc_renamingAdaptResources = new GridBagConstraints();
        gbc_renamingAdaptResources.anchor = GridBagConstraints.WEST;
        gbc_renamingAdaptResources.insets = new Insets(0, 0, 5, 5);
        gbc_renamingAdaptResources.gridx = 0;
        gbc_renamingAdaptResources.gridy = 2;
        renamingAdaptResources.setEnabled(false);
        renamingPanel.add(renamingAdaptResources, gbc_renamingAdaptResources);

        renamingResourcesField = new JTextField();
        GridBagConstraints gbc_renamingResourcesField = new GridBagConstraints();
        gbc_renamingResourcesField.gridwidth = 2;
        gbc_renamingResourcesField.insets = new Insets(0, 0, 5, 5);
        gbc_renamingResourcesField.fill = GridBagConstraints.HORIZONTAL;
        gbc_renamingResourcesField.gridx = 14;
        gbc_renamingResourcesField.gridy = 2;
        renamingResourcesField.setEditable(false);
        renamingPanel.add(renamingResourcesField, gbc_renamingResourcesField);
        renamingResourcesField.setColumns(10);

        renamingEnabledCheckBox = new JCheckBox("Enabled");
        GridBagConstraints gbc_renamingEnabledCheckBox = new GridBagConstraints();
        gbc_renamingEnabledCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_renamingEnabledCheckBox.anchor = GridBagConstraints.WEST;
        gbc_renamingEnabledCheckBox.gridx = 0;
        gbc_renamingEnabledCheckBox.gridy = 0;
        renamingEnabledCheckBox.addActionListener((e) -> {
            boolean enable = renamingEnabledCheckBox.isSelected();
            renamingRepackageCheckBox.setEnabled(enable);
            renamingRepackageField.setEditable(enable);
            renamingAdaptResources.setEnabled(enable);
            renamingResourcesField.setEditable(enable);
        });
        renamingPanel.add(renamingEnabledCheckBox, gbc_renamingEnabledCheckBox);

        JPanel otherPanel = new JPanel();
        GridBagConstraints gbc_otherPanel = new GridBagConstraints();
        gbc_otherPanel.fill = GridBagConstraints.BOTH;
        gbc_otherPanel.gridx = 0;
        gbc_otherPanel.gridy = 2;
        otherPanel.setBorder(new TitledBorder("Other"));
        this.add(otherPanel, gbc_otherPanel);
        GridBagLayout gbl_otherPanel = new GridBagLayout();
        gbl_otherPanel.columnWidths = new int[]{74, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_otherPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_otherPanel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_otherPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        otherPanel.setLayout(gbl_otherPanel);

        invokeDynamicComboBox = new JComboBox<>();
        GridBagConstraints gbc_invokeDynamicComboBox = new GridBagConstraints();
        gbc_invokeDynamicComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_invokeDynamicComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_invokeDynamicComboBox.gridx = 15;
        gbc_invokeDynamicComboBox.gridy = 0;
        invokeDynamicComboBox.addItem("Light");
        invokeDynamicComboBox.addItem("Normal");
        invokeDynamicComboBox.addItem("Heavy");
        invokeDynamicComboBox.setEnabled(false);
        otherPanel.add(invokeDynamicComboBox, gbc_invokeDynamicComboBox);

        invokeDynamicCheckBox = new JCheckBox("InvokeDynamic");
        GridBagConstraints gbc_invokeDynamicCheckBox = new GridBagConstraints();
        gbc_invokeDynamicCheckBox.anchor = GridBagConstraints.WEST;
        gbc_invokeDynamicCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_invokeDynamicCheckBox.gridx = 0;
        gbc_invokeDynamicCheckBox.gridy = 0;
        invokeDynamicCheckBox.addActionListener((e) -> invokeDynamicComboBox.setEnabled(invokeDynamicCheckBox.isSelected()));
        otherPanel.add(invokeDynamicCheckBox, gbc_invokeDynamicCheckBox);

        flowComboBox = new JComboBox<>();
        GridBagConstraints gbc_flowComboBox = new GridBagConstraints();
        gbc_flowComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_flowComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_flowComboBox.gridx = 15;
        gbc_flowComboBox.gridy = 1;
        flowComboBox.addItem("Light");
        flowComboBox.addItem("Normal");
        flowComboBox.addItem("Heavy");
        flowComboBox.setEnabled(false);
        otherPanel.add(flowComboBox, gbc_flowComboBox);

        flowCheckBox = new JCheckBox("Flow Obfuscation");
        GridBagConstraints gbc_flowCheckBox = new GridBagConstraints();
        gbc_flowCheckBox.anchor = GridBagConstraints.WEST;
        gbc_flowCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_flowCheckBox.gridx = 0;
        gbc_flowCheckBox.gridy = 1;
        flowCheckBox.addActionListener((e) -> flowComboBox.setEnabled(flowCheckBox.isSelected()));
        otherPanel.add(flowCheckBox, gbc_flowCheckBox);

        numberObfuscationComboBox = new JComboBox<>();
        GridBagConstraints gbc_numberObfuscationComboBox = new GridBagConstraints();
        gbc_numberObfuscationComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_numberObfuscationComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_numberObfuscationComboBox.gridx = 15;
        gbc_numberObfuscationComboBox.gridy = 2;
        numberObfuscationComboBox.addItem("Light");
        numberObfuscationComboBox.addItem("Normal");
        numberObfuscationComboBox.addItem("Heavy");
        numberObfuscationComboBox.setEnabled(false);
        otherPanel.add(numberObfuscationComboBox, gbc_numberObfuscationComboBox);

        numberObfuscationCheckBox = new JCheckBox("Number Obfuscation");
        GridBagConstraints gbc_numberObfuscationCheckBox = new GridBagConstraints();
        gbc_numberObfuscationCheckBox.anchor = GridBagConstraints.WEST;
        gbc_numberObfuscationCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_numberObfuscationCheckBox.gridx = 0;
        gbc_numberObfuscationCheckBox.gridy = 2;
        numberObfuscationCheckBox.addActionListener((e) -> numberObfuscationComboBox.setEnabled(numberObfuscationCheckBox.isSelected()));
        otherPanel.add(numberObfuscationCheckBox, gbc_numberObfuscationCheckBox);

        localVarsRemove = new JCheckBox("Remove");
        GridBagConstraints gbc_localVarsRemove = new GridBagConstraints();
        gbc_localVarsRemove.fill = GridBagConstraints.HORIZONTAL;
        gbc_localVarsRemove.insets = new Insets(0, 0, 5, 0);
        gbc_localVarsRemove.gridx = 15;
        gbc_localVarsRemove.gridy = 3;
        localVarsRemove.setEnabled(false);
        otherPanel.add(localVarsRemove, gbc_localVarsRemove);

        localVarCheckBox = new JCheckBox("Local Variables");
        GridBagConstraints gbc_localVarCheckBox = new GridBagConstraints();
        gbc_localVarCheckBox.anchor = GridBagConstraints.WEST;
        gbc_localVarCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_localVarCheckBox.gridx = 0;
        gbc_localVarCheckBox.gridy = 3;
        localVarCheckBox.addActionListener((e) -> localVarsRemove.setEnabled(localVarCheckBox.isSelected()));
        otherPanel.add(localVarCheckBox, gbc_localVarCheckBox);

        lineNumbersRemove = new JCheckBox("Remove");
        GridBagConstraints gbc_lineNumbersRemove = new GridBagConstraints();
        gbc_lineNumbersRemove.fill = GridBagConstraints.HORIZONTAL;
        gbc_lineNumbersRemove.insets = new Insets(0, 0, 5, 0);
        gbc_lineNumbersRemove.gridx = 15;
        gbc_lineNumbersRemove.gridy = 4;
        lineNumbersRemove.setEnabled(false);
        otherPanel.add(lineNumbersRemove, gbc_lineNumbersRemove);

        lineNumbersCheckBox = new JCheckBox("Line Numbers");
        GridBagConstraints gbc_lineNumbersCheckBox = new GridBagConstraints();
        gbc_lineNumbersCheckBox.anchor = GridBagConstraints.WEST;
        gbc_lineNumbersCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_lineNumbersCheckBox.gridx = 0;
        gbc_lineNumbersCheckBox.gridy = 4;
        lineNumbersCheckBox.addActionListener((e) -> lineNumbersRemove.setEnabled(lineNumbersCheckBox.isSelected()));
        otherPanel.add(lineNumbersCheckBox, gbc_lineNumbersCheckBox);

        sourceNameRemove = new JCheckBox("Remove");
        GridBagConstraints gbc_sourceNameRemove = new GridBagConstraints();
        gbc_sourceNameRemove.fill = GridBagConstraints.HORIZONTAL;
        gbc_sourceNameRemove.insets = new Insets(0, 0, 5, 0);
        gbc_sourceNameRemove.gridx = 15;
        gbc_sourceNameRemove.gridy = 5;
        sourceNameRemove.setEnabled(false);
        otherPanel.add(sourceNameRemove, gbc_sourceNameRemove);

        sourceNameCheckBox = new JCheckBox("Source Name");
        GridBagConstraints gbc_sourceNameCheckBox = new GridBagConstraints();
        gbc_sourceNameCheckBox.anchor = GridBagConstraints.WEST;
        gbc_sourceNameCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_sourceNameCheckBox.gridx = 0;
        gbc_sourceNameCheckBox.gridy = 5;
        sourceNameCheckBox.addActionListener((e) -> sourceNameRemove.setEnabled(sourceNameCheckBox.isSelected()));
        otherPanel.add(sourceNameCheckBox, gbc_sourceNameCheckBox);

        sourceDebugRemove = new JCheckBox("Remove");
        GridBagConstraints gbc_sourceDebugRemove = new GridBagConstraints();
        gbc_sourceDebugRemove.fill = GridBagConstraints.HORIZONTAL;
        gbc_sourceDebugRemove.insets = new Insets(0, 0, 5, 0);
        gbc_sourceDebugRemove.gridx = 15;
        gbc_sourceDebugRemove.gridy = 6;
        sourceDebugRemove.setEnabled(false);
        otherPanel.add(sourceDebugRemove, gbc_sourceDebugRemove);

        sourceDebugCheckBox = new JCheckBox("Source Debug");
        GridBagConstraints gbc_sourceDebugCheckBox = new GridBagConstraints();
        gbc_sourceDebugCheckBox.anchor = GridBagConstraints.WEST;
        gbc_sourceDebugCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_sourceDebugCheckBox.gridx = 0;
        gbc_sourceDebugCheckBox.gridy = 6;
        sourceDebugCheckBox.addActionListener((e) -> sourceDebugRemove.setEnabled(sourceDebugCheckBox.isSelected()));
        otherPanel.add(sourceDebugCheckBox, gbc_sourceDebugCheckBox);

        hideCodeCheckBox = new JCheckBox("Hide Code");
        GridBagConstraints gbc_hideCodeCheckBox = new GridBagConstraints();
        gbc_hideCodeCheckBox.anchor = GridBagConstraints.WEST;
        gbc_hideCodeCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_hideCodeCheckBox.gridx = 0;
        gbc_hideCodeCheckBox.gridy = 7;
        otherPanel.add(hideCodeCheckBox, gbc_hideCodeCheckBox);

        shufflerCheckBox = new JCheckBox("Shuffler");
        GridBagConstraints gbc_shufflerCheckBox = new GridBagConstraints();
        gbc_shufflerCheckBox.anchor = GridBagConstraints.WEST;
        gbc_shufflerCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_shufflerCheckBox.gridx = 0;
        gbc_shufflerCheckBox.gridy = 8;
        otherPanel.add(shufflerCheckBox, gbc_shufflerCheckBox);

        crasherCheckBox = new JCheckBox("Crasher");
        GridBagConstraints gbc_crasherCheckBox = new GridBagConstraints();
        gbc_crasherCheckBox.anchor = GridBagConstraints.WEST;
        gbc_crasherCheckBox.insets = new Insets(0, 0, 0, 5);
        gbc_crasherCheckBox.gridx = 0;
        gbc_crasherCheckBox.gridy = 9;
        otherPanel.add(crasherCheckBox, gbc_crasherCheckBox);
    }

    public StringEncryption getStringEncryption() {
        if (stringEncryptionEnabledCheckBox.isSelected()) {
            StringEncryptionSetup setup = new StringEncryptionSetup(stringExclusions);
            StringEncryption encryptionTransformer;

            switch (stringEncryptionTypeSelector.getSelectedIndex()) {
                case 0: {
                    encryptionTransformer = new LightStringEncryption(setup);
                    break;
                }
                case 1: {
                    encryptionTransformer = new NormalStringEncryption(setup);
                    break;
                }
                case 2: {
                    encryptionTransformer = new HeavyStringEncryption(setup);
                    break;
                }
                default: {
                    throw new IllegalStateException(String.format("Bad string encryption type %d.", stringEncryptionTypeSelector.getSelectedIndex()));
                }
            }

            return encryptionTransformer;
        } else {
            return null;
        }
    }

    public StringPool getStringPool() {
        return (stringEncryptionEnabledCheckBox.isSelected() && stringPoolCheckBox.isSelected()) ? new StringPool(new StringEncryptionSetup(stringExclusions)) : null;
    }

    public Renamer getRenamer() {
        if (renamingEnabledCheckBox.isSelected()) {
            String[] resources = (renamingAdaptResources.isSelected() && renamingResourcesField.getText() != null && !renamingResourcesField.getText().isEmpty()) ? renamingResourcesField.getText().split(", ") : null;
            String repackageName = (renamingRepackageCheckBox.isSelected() && renamingRepackageField.getText() != null && !renamingRepackageField.getText().isEmpty()) ? renamingRepackageField.getText() : null;

            return new Renamer(new RenamerSetup(resources, repackageName));
        } else {
            return null;
        }
    }

    public InvokeDynamic getInvokeDynamic() {
        if (invokeDynamicCheckBox.isSelected()) {
            switch (invokeDynamicComboBox.getSelectedIndex()) {
                case 0: {
                    return new LightInvokeDynamic();
                }
                case 1: {
                    return new NormalInvokeDynamic();
                }
                case 2: {
                    return new HeavyInvokeDynamic();
                }
                default: {
                    throw new IllegalStateException(String.format("Bad invokedynamic type %d.", invokeDynamicComboBox.getSelectedIndex()));
                }
            }
        } else {
            return null;
        }
    }

    public FlowObfuscation getFlowObfuscation() {
        if (flowCheckBox.isSelected()) {
            switch (flowComboBox.getSelectedIndex()) {
                case 0: {
                    return new LightFlowObfuscation();
                }
                case 1: {
                    return new NormalFlowObfuscation();
                }
                case 2: {
                    return new HeavyFlowObfuscation();
                }
                default: {
                    throw new IllegalStateException(String.format("Bad flow obfuscation type %d.", flowComboBox.getSelectedIndex()));
                }
            }
        } else {
            return null;
        }
    }

    public NumberObfuscation getNumberObfuscation() {
        if (numberObfuscationCheckBox.isSelected()) {
            switch (numberObfuscationComboBox.getSelectedIndex()) {
                case 0: {
                    return new LightNumberObfuscation();
                }
                case 1: {
                    return new NormalNumberObfuscation();
                }
                case 2: {
                    return new HeavyNumberObfuscation();
                }
                default: {
                    throw new IllegalStateException(String.format("Bad number obfuscation type %d.", flowComboBox.getSelectedIndex()));
                }
            }
        } else {
            return null;
        }
    }

    public LocalVariables getLocalVarObfuscation() {
        return (localVarCheckBox.isSelected()) ? new LocalVariables(localVarsRemove.isSelected()) : null;
    }

    public LineNumbers getLineNumberObfuscation() {
        return (lineNumbersCheckBox.isSelected()) ? new LineNumbers(lineNumbersRemove.isSelected()) : null;
    }

    public SourceName getSourceNameObfuscation() {
        return (sourceNameCheckBox.isSelected()) ? new SourceName(sourceNameRemove.isSelected()) : null;
    }

    public SourceDebug getSourceDebugObfuscation() {
        return (sourceDebugCheckBox.isSelected()) ? new SourceDebug(sourceDebugRemove.isSelected()) : null;
    }

    public HideCode getHideCodeObfuscation() {
        return (hideCodeCheckBox.isSelected()) ? new HideCode() : null;
    }

    public MemberShuffler getShuffler() {
        return (shufflerCheckBox.isSelected()) ? new MemberShuffler() : null;
    }

    public Crasher getCrasher() {
        return (crasherCheckBox.isSelected()) ? new Crasher() : null;
    }

    public void setSettings(SessionInfo info) {
        stringEncryptionEnabledCheckBox.setSelected(false);
        stringEncryptionTypeSelector.setSelectedIndex(0);
        stringEncryptionTypeSelector.setEnabled(false);
        stringPoolCheckBox.setSelected(false);
        stringPoolCheckBox.setEnabled(false);
        stringEncryptionExclusionButtons.setEnabled(false);
        stringExclusions.clear();

        renamingEnabledCheckBox.setSelected(false);
        renamingRepackageField.setText(null);
        renamingRepackageField.setEditable(false);
        renamingResourcesField.setText(null);
        renamingResourcesField.setEditable(false);
        renamingAdaptResources.setSelected(false);
        renamingAdaptResources.setEnabled(false);
        renamingRepackageCheckBox.setSelected(false);
        renamingRepackageCheckBox.setEnabled(false);

        invokeDynamicCheckBox.setSelected(false);
        invokeDynamicComboBox.setSelectedIndex(0);
        invokeDynamicComboBox.setEnabled(false);

        flowCheckBox.setSelected(false);
        flowComboBox.setSelectedIndex(0);
        flowComboBox.setEnabled(false);

        numberObfuscationCheckBox.setSelected(false);
        numberObfuscationComboBox.setSelectedIndex(0);
        numberObfuscationComboBox.setEnabled(false);

        localVarCheckBox.setSelected(false);
        localVarsRemove.setSelected(false);
        localVarsRemove.setEnabled(false);

        lineNumbersCheckBox.setSelected(false);
        lineNumbersRemove.setSelected(false);
        lineNumbersRemove.setEnabled(false);

        sourceNameCheckBox.setSelected(false);
        sourceNameRemove.setSelected(false);
        sourceNameRemove.setEnabled(false);

        sourceDebugCheckBox.setSelected(false);
        sourceDebugRemove.setSelected(false);
        sourceDebugRemove.setEnabled(false);

        hideCodeCheckBox.setSelected(false);
        shufflerCheckBox.setSelected(false);
        crasherCheckBox.setSelected(false);

        if (info.getTransformers() != null) {
            info.getTransformers().stream().filter(Objects::nonNull).forEach(transformer -> {
                if (transformer instanceof StringEncryption) {
                    stringEncryptionEnabledCheckBox.setSelected(true);
                    stringEncryptionTypeSelector.setEnabled(true);
                    stringEncryptionExclusionButtons.setEnabled(true);
                    stringPoolCheckBox.setEnabled(true);

                    if (transformer instanceof LightStringEncryption) {
                        stringEncryptionTypeSelector.setSelectedIndex(0);
                    } else if (transformer instanceof NormalStringEncryption) {
                        stringEncryptionTypeSelector.setSelectedIndex(1);
                    } else if (transformer instanceof HeavyStringEncryption) {
                        stringEncryptionTypeSelector.setSelectedIndex(2);
                    } else if (transformer instanceof StringPool) {
                        stringPoolCheckBox.setSelected(true);
                    }
                    if (stringExclusions.isEmpty()) {
                        stringExclusions.addAll(((StringEncryption) transformer).getExcludedStrings());
                    }
                } else if (transformer instanceof Renamer) {
                    RenamerSetup setup = ((Renamer) transformer).getSetup();

                    renamingEnabledCheckBox.setSelected(true);
                    renamingRepackageField.setText(setup.getRepackageName());
                    renamingRepackageField.setEditable(true);
                    renamingAdaptResources.setEnabled(true);
                    renamingRepackageCheckBox.setEnabled(true);
                    if (setup.getAdaptTheseResources().length > 0) {
                        String str = Arrays.toString(setup.getAdaptTheseResources());
                        if (str.length() > 1) {
                            str = str.substring(1, str.length() - 1);
                        }
                        renamingResourcesField.setText(str);
                    }
                    renamingResourcesField.setEditable(true);
                } else if (transformer instanceof InvokeDynamic) {
                    invokeDynamicCheckBox.setSelected(true);
                    invokeDynamicComboBox.setEnabled(true);

                    if (transformer instanceof LightInvokeDynamic) {
                        invokeDynamicComboBox.setSelectedIndex(0);
                    } else if (transformer instanceof NormalInvokeDynamic) {
                        invokeDynamicComboBox.setSelectedIndex(1);
                    } else if (transformer instanceof HeavyInvokeDynamic) {
                        invokeDynamicComboBox.setSelectedIndex(2);
                    }
                } else if (transformer instanceof FlowObfuscation) {
                    flowCheckBox.setSelected(true);
                    flowComboBox.setEnabled(true);

                    if (transformer instanceof LightFlowObfuscation) {
                        flowComboBox.setSelectedIndex(0);
                    } else if (transformer instanceof HeavyFlowObfuscation) {
                        flowComboBox.setSelectedIndex(2);
                    } else if (transformer instanceof NormalFlowObfuscation) {
                        flowComboBox.setSelectedIndex(1);
                    }
                } else if (transformer instanceof NumberObfuscation) {
                    numberObfuscationCheckBox.setSelected(true);
                    numberObfuscationComboBox.setEnabled(true);

                    if (transformer instanceof LightNumberObfuscation) {
                        numberObfuscationComboBox.setSelectedIndex(0);
                    } else if (transformer instanceof NormalNumberObfuscation) {
                        numberObfuscationComboBox.setSelectedIndex(1);
                    } else if (transformer instanceof HeavyNumberObfuscation) {
                        numberObfuscationComboBox.setSelectedIndex(2);
                    }
                } else if (transformer instanceof LocalVariables) {
                    localVarCheckBox.setSelected(true);
                    localVarsRemove.setEnabled(true);
                    localVarsRemove.setSelected(((LocalVariables) transformer).isRemove());
                } else if (transformer instanceof LineNumbers) {
                    lineNumbersCheckBox.setSelected(true);
                    lineNumbersRemove.setEnabled(true);
                    lineNumbersRemove.setSelected(((LineNumbers) transformer).isRemove());
                } else if (transformer instanceof SourceName) {
                    sourceNameCheckBox.setSelected(true);
                    sourceNameRemove.setEnabled(true);
                    sourceNameRemove.setSelected(((SourceName) transformer).isRemove());
                } else if (transformer instanceof SourceDebug) {
                    sourceDebugCheckBox.setSelected(true);
                    sourceDebugRemove.setEnabled(true);
                    sourceDebugRemove.setSelected(((SourceDebug) transformer).isRemove());
                } else if (transformer instanceof HideCode) {
                    hideCodeCheckBox.setSelected(true);
                } else if (transformer instanceof MemberShuffler) {
                    shufflerCheckBox.setSelected(true);
                } else if (transformer instanceof Crasher) {
                    crasherCheckBox.setSelected(true);
                }
            });
        }
    }
}
