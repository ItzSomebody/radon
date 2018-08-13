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
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import me.itzsomebody.radon.gui.tabs.ObfuscationTab;

public class StringEncryptionExclusionGUI extends JFrame {
    public StringEncryptionExclusionGUI() {
        setTitle("String Exclusions");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignored
        }
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        JScrollPane exclusionScrollPane = new JScrollPane();
        GridBagConstraints gbc_exclusionScrollPane = new GridBagConstraints();
        gbc_exclusionScrollPane.gridwidth = 3;
        gbc_exclusionScrollPane.insets = new Insets(5, 5, 5, 5);
        gbc_exclusionScrollPane.fill = GridBagConstraints.BOTH;
        gbc_exclusionScrollPane.gridx = 0;
        gbc_exclusionScrollPane.gridy = 0;
        contentPane.add(exclusionScrollPane, gbc_exclusionScrollPane);

        DefaultListModel<String> exclusionList = new DefaultListModel<>();
        for (String s : ObfuscationTab.stringExclusions) {
            exclusionList.addElement(s);
        }
        JList<String> exclusionJList = new JList<>(exclusionList);
        exclusionScrollPane.setViewportView(exclusionJList);

        JTextField exclusionField = new JTextField();
        GridBagConstraints gbc_exclusionField = new GridBagConstraints();
        gbc_exclusionField.insets = new Insets(0, 5, 5, 5);
        gbc_exclusionField.fill = GridBagConstraints.HORIZONTAL;
        gbc_exclusionField.gridx = 0;
        gbc_exclusionField.gridy = 1;
        contentPane.add(exclusionField, gbc_exclusionField);
        exclusionField.setColumns(10);

        JButton exclusionAddButton = new JButton("Add");
        GridBagConstraints gbc_exclusionAddButton = new GridBagConstraints();
        gbc_exclusionAddButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_exclusionAddButton.insets = new Insets(0, 0, 5, 5);
        gbc_exclusionAddButton.gridx = 1;
        gbc_exclusionAddButton.gridy = 1;
        exclusionAddButton.addActionListener((e) -> {
            if (exclusionField.getText() != null && !exclusionField.getText().isEmpty()) {
                if (!ObfuscationTab.stringExclusions.contains(exclusionField.getText())) {
                    exclusionList.addElement(exclusionField.getText());
                }
                exclusionField.setText(null);
            }
        });
        contentPane.add(exclusionAddButton, gbc_exclusionAddButton);

        JButton exclusionRemoveButton = new JButton("Remove");
        GridBagConstraints gbc_exclusionRemoveButton = new GridBagConstraints();
        gbc_exclusionRemoveButton.insets = new Insets(0, 0, 5, 5);
        gbc_exclusionRemoveButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_exclusionRemoveButton.gridx = 2;
        gbc_exclusionRemoveButton.gridy = 1;
        exclusionRemoveButton.addActionListener((e) -> {
            List<String> removeList = exclusionJList.getSelectedValuesList();
            if (removeList.isEmpty())
                return;

            for (String s : removeList) {
                exclusionList.removeElement(s);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                for (int i = 0; i < exclusionList.size(); i++) {
                    if (!ObfuscationTab.stringExclusions.contains(exclusionList.get(i))) {
                        ObfuscationTab.stringExclusions.add(exclusionList.get(i));
                    }
                }
                dispose();
            }
        });
        contentPane.add(exclusionRemoveButton, gbc_exclusionRemoveButton);
        setVisible(true);
    }
}
