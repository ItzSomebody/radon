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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import me.itzsomebody.radon.Dictionaries;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.transformers.miscellaneous.expiration.Expiration;
import me.itzsomebody.radon.transformers.miscellaneous.expiration.ExpirationSetup;

public class MiscellaneousTab extends JPanel {
    private JCheckBox expirationSwingCheckBox;
    private JTextField expirationMessageField;
    private JTextField expirationExpiresField;
    private JCheckBox expirationEnabledCheckBox;
    private JComboBox<String> dictionaryComboBox;
    private JTextField trashClassesField;

    public MiscellaneousTab() {
        GridBagLayout gbl_this = new GridBagLayout();
        gbl_this.columnWidths = new int[]{0, 0};
        gbl_this.rowHeights = new int[]{0, 0, 0};
        gbl_this.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_this.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        this.setLayout(gbl_this);

        JPanel expirationPanel = new JPanel();
        GridBagConstraints gbc_expirationPanel = new GridBagConstraints();
        gbc_expirationPanel.insets = new Insets(0, 0, 5, 0);
        gbc_expirationPanel.fill = GridBagConstraints.BOTH;
        gbc_expirationPanel.gridx = 0;
        gbc_expirationPanel.gridy = 0;
        this.add(expirationPanel, gbc_expirationPanel);
        GridBagLayout gbl_expirationPanel = new GridBagLayout();
        gbl_expirationPanel.columnWidths = new int[]{0, 0};
        gbl_expirationPanel.rowHeights = new int[]{0, 29, 0};
        gbl_expirationPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_expirationPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        expirationPanel.setBorder(new TitledBorder("Expiration"));
        expirationPanel.setLayout(gbl_expirationPanel);

        JPanel expirationSetupPanel = new JPanel();
        GridBagConstraints gbc_expirationSetupPanel = new GridBagConstraints();
        gbc_expirationSetupPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_expirationSetupPanel.gridx = 0;
        gbc_expirationSetupPanel.gridy = 1;
        expirationSetupPanel.setBorder(new TitledBorder("Setup"));
        expirationPanel.add(expirationSetupPanel, gbc_expirationSetupPanel);
        GridBagLayout gbl_expirationSetupPanel = new GridBagLayout();
        gbl_expirationSetupPanel.columnWidths = new int[]{0, 0, 0};
        gbl_expirationSetupPanel.rowHeights = new int[]{0, 0, 0, 0};
        gbl_expirationSetupPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gbl_expirationSetupPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        expirationSetupPanel.setLayout(gbl_expirationSetupPanel);

        expirationSwingCheckBox = new JCheckBox("Inject JOptionPane Message");
        GridBagConstraints gbc_expirationSwingCheckBox = new GridBagConstraints();
        gbc_expirationSwingCheckBox.anchor = GridBagConstraints.WEST;
        gbc_expirationSwingCheckBox.gridwidth = 2;
        gbc_expirationSwingCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_expirationSwingCheckBox.gridx = 0;
        gbc_expirationSwingCheckBox.gridy = 0;
        expirationSwingCheckBox.setEnabled(false);
        expirationSetupPanel.add(expirationSwingCheckBox, gbc_expirationSwingCheckBox);

        JLabel expirationMessageLabel = new JLabel("Message:");
        GridBagConstraints gbc_expirationMessageLabel = new GridBagConstraints();
        gbc_expirationMessageLabel.anchor = GridBagConstraints.EAST;
        gbc_expirationMessageLabel.insets = new Insets(0, 5, 5, 5);
        gbc_expirationMessageLabel.gridx = 0;
        gbc_expirationMessageLabel.gridy = 1;
        expirationSetupPanel.add(expirationMessageLabel, gbc_expirationMessageLabel);

        expirationMessageField = new JTextField();
        GridBagConstraints gbc_expirationMessageField = new GridBagConstraints();
        gbc_expirationMessageField.insets = new Insets(0, 0, 5, 5);
        gbc_expirationMessageField.fill = GridBagConstraints.HORIZONTAL;
        gbc_expirationMessageField.gridx = 1;
        gbc_expirationMessageField.gridy = 1;
        expirationMessageField.setEditable(false);
        expirationSetupPanel.add(expirationMessageField, gbc_expirationMessageField);
        expirationMessageField.setColumns(10);

        JLabel expirationExpiresLabel = new JLabel("Expires:");
        GridBagConstraints gbc_expirationExpiresLabel = new GridBagConstraints();
        gbc_expirationExpiresLabel.anchor = GridBagConstraints.EAST;
        gbc_expirationExpiresLabel.insets = new Insets(0, 0, 0, 5);
        gbc_expirationExpiresLabel.gridx = 0;
        gbc_expirationExpiresLabel.gridy = 2;
        expirationSetupPanel.add(expirationExpiresLabel, gbc_expirationExpiresLabel);

        expirationExpiresField = new JTextField();
        GridBagConstraints gbc_expirationExpiresField = new GridBagConstraints();
        gbc_expirationExpiresField.insets = new Insets(0, 0, 5, 5);
        gbc_expirationExpiresField.fill = GridBagConstraints.HORIZONTAL;
        gbc_expirationExpiresField.gridx = 1;
        gbc_expirationExpiresField.gridy = 2;
        expirationExpiresField.setEditable(false);
        expirationSetupPanel.add(expirationExpiresField, gbc_expirationExpiresField);
        expirationExpiresField.setColumns(10);

        expirationEnabledCheckBox = new JCheckBox("Enabled");
        GridBagConstraints gbc_expirationEnabledCheckBox = new GridBagConstraints();
        gbc_expirationEnabledCheckBox.anchor = GridBagConstraints.WEST;
        gbc_expirationEnabledCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_expirationEnabledCheckBox.gridx = 0;
        gbc_expirationEnabledCheckBox.gridy = 0;
        expirationEnabledCheckBox.addActionListener((e) -> {
            boolean enable = expirationEnabledCheckBox.isSelected();

            expirationSwingCheckBox.setEnabled(enable);
            expirationMessageField.setEditable(enable);
            expirationExpiresField.setEditable(enable);
        });
        expirationPanel.add(expirationEnabledCheckBox, gbc_expirationEnabledCheckBox);

        JPanel miscOtherPanel = new JPanel();
        GridBagConstraints gbc_miscOtherPanel = new GridBagConstraints();
        gbc_miscOtherPanel.fill = GridBagConstraints.BOTH;
        gbc_miscOtherPanel.gridx = 0;
        gbc_miscOtherPanel.gridy = 1;
        this.add(miscOtherPanel, gbc_miscOtherPanel);
        GridBagLayout gbl_miscOtherPanel = new GridBagLayout();
        gbl_miscOtherPanel.columnWidths = new int[]{0, 0, 0};
        gbl_miscOtherPanel.rowHeights = new int[]{32, 0, 0};
        gbl_miscOtherPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gbl_miscOtherPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        miscOtherPanel.setBorder(new TitledBorder("Other"));
        miscOtherPanel.setLayout(gbl_miscOtherPanel);

        JButton garbagCollectorButton = new JButton("Garbage Collector");
        GridBagConstraints gbc_garbagCollectorButton = new GridBagConstraints();
        gbc_garbagCollectorButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_garbagCollectorButton.insets = new Insets(0, 5, 0, 5);
        gbc_garbagCollectorButton.gridx = 0;
        gbc_garbagCollectorButton.gridy = 0;
        garbagCollectorButton.addActionListener((e) -> SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000) + "mb in use before garbage collection.");
            System.gc();
        }));
        miscOtherPanel.add(garbagCollectorButton, gbc_garbagCollectorButton);

        JPanel dictionaryPanel = new JPanel();
        GridBagConstraints gbc_dictionaryPanel = new GridBagConstraints();
        gbc_dictionaryPanel.insets = new Insets(0, 0, 5, 0);
        gbc_dictionaryPanel.anchor = GridBagConstraints.EAST;
        gbc_dictionaryPanel.fill = GridBagConstraints.VERTICAL;
        gbc_dictionaryPanel.gridx = 1;
        gbc_dictionaryPanel.gridy = 0;
        dictionaryPanel.setBorder(new TitledBorder("Dictionary"));
        miscOtherPanel.add(dictionaryPanel, gbc_dictionaryPanel);
        GridBagLayout gbl_dictionaryPanel = new GridBagLayout();
        gbl_dictionaryPanel.columnWidths = new int[]{0, 0};
        gbl_dictionaryPanel.rowHeights = new int[]{0, 0};
        gbl_dictionaryPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_dictionaryPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        dictionaryPanel.setLayout(gbl_dictionaryPanel);

        dictionaryComboBox = new JComboBox<>();
        GridBagConstraints gbc_dictionaryComboBox = new GridBagConstraints();
        gbc_dictionaryComboBox.insets = new Insets(0, 5, 5, 5);
        gbc_dictionaryComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_dictionaryComboBox.gridx = 0;
        gbc_dictionaryComboBox.gridy = 0;
        dictionaryComboBox.addItem(Dictionaries.SPACES.getValue());
        dictionaryComboBox.addItem(Dictionaries.UNRECOGNIZED.getValue());
        dictionaryComboBox.addItem(Dictionaries.ALPHABETICAL.getValue());
        dictionaryComboBox.addItem(Dictionaries.ALPHANUMERIC.getValue());
        dictionaryPanel.add(dictionaryComboBox, gbc_dictionaryComboBox);

        JPanel trashClassPanel = new JPanel();
        GridBagConstraints gbc_trashClassPanel = new GridBagConstraints();
        gbc_trashClassPanel.insets = new Insets(0, 0, 5, 0);
        gbc_trashClassPanel.anchor = GridBagConstraints.EAST;
        gbc_trashClassPanel.fill = GridBagConstraints.VERTICAL;
        gbc_trashClassPanel.gridx = 1;
        gbc_trashClassPanel.gridy = 1;
        trashClassPanel.setBorder(new TitledBorder("Trash Classes"));
        miscOtherPanel.add(trashClassPanel, gbc_trashClassPanel);
        GridBagLayout gbl_trashClassPanel = new GridBagLayout();
        gbl_trashClassPanel.columnWidths = new int[]{0, 0};
        gbl_trashClassPanel.rowHeights = new int[]{0, 0};
        gbl_trashClassPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_trashClassPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        trashClassPanel.setLayout(gbl_trashClassPanel);

        trashClassesField = new JTextField();
        GridBagConstraints gbc_trashClassesField = new GridBagConstraints();
        gbc_trashClassesField.insets = new Insets(0, 0, 5, 5);
        gbc_trashClassesField.fill = GridBagConstraints.HORIZONTAL;
        gbc_trashClassesField.gridx = 0;
        gbc_trashClassesField.gridy = 0;
        trashClassesField.setText("0");
        trashClassPanel.add(trashClassesField, gbc_trashClassesField);
        trashClassesField.setColumns(10);

        JButton aboutButton = new JButton("About");
        GridBagConstraints gbc_aboutButton = new GridBagConstraints();
        gbc_aboutButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_aboutButton.insets = new Insets(0, 5, 5, 5);
        gbc_aboutButton.gridx = 0;
        gbc_aboutButton.gridy = 1;
        aboutButton.addActionListener((e) -> JOptionPane.showMessageDialog(null, Main.PROPAGANDA_GARBAGE));
        miscOtherPanel.add(aboutButton, gbc_aboutButton);
    }

    public Expiration getExpiration() {
        try {
            return (expirationEnabledCheckBox.isSelected()) ? new Expiration(new ExpirationSetup(expirationMessageField.getText(),
                new SimpleDateFormat("MM/dd/yyyy").parse(expirationExpiresField.getText()).getTime(), expirationSwingCheckBox.isSelected())) : null;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public int getTrashClasses() {
        return Integer.valueOf(trashClassesField.getText());
    }

    public Dictionaries getDictionary() {
        return Dictionaries.intToDictionary(dictionaryComboBox.getSelectedIndex());
    }
}
