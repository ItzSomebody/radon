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
import java.util.List;
import javax.swing.*;
import me.itzsomebody.radon.SessionInfo;
import me.itzsomebody.radon.exclusions.Exclusion;
import me.itzsomebody.radon.exclusions.ExclusionManager;
import me.itzsomebody.radon.exclusions.ExclusionType;

public class ExclusionsTab extends JPanel {
    private DefaultListModel<String> exclusions;

    public ExclusionsTab() {
        GridBagLayout gbl_this = new GridBagLayout();
        gbl_this.columnWidths = new int[]{0, 0, 0, 0, 0};
        gbl_this.rowHeights = new int[]{0, 0, 0};
        gbl_this.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_this.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        this.setLayout(gbl_this);

        JScrollPane exclusionScrollPane = new JScrollPane();
        GridBagConstraints gbc_exclusionScrollPane = new GridBagConstraints();
        gbc_exclusionScrollPane.gridwidth = 4;
        gbc_exclusionScrollPane.insets = new Insets(5, 5, 5, 5);
        gbc_exclusionScrollPane.fill = GridBagConstraints.BOTH;
        gbc_exclusionScrollPane.gridx = 0;
        gbc_exclusionScrollPane.gridy = 0;
        this.add(exclusionScrollPane, gbc_exclusionScrollPane);

        exclusions = new DefaultListModel<>();
        JList<String> exclusionList = new JList<>(exclusions);
        exclusionScrollPane.setViewportView(exclusionList);

        JComboBox<String> exclusionComboBox = new JComboBox<>();
        GridBagConstraints gbc_exclusionComboBox = new GridBagConstraints();
        gbc_exclusionComboBox.insets = new Insets(0, 5, 5, 5);
        gbc_exclusionComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_exclusionComboBox.gridx = 0;
        gbc_exclusionComboBox.gridy = 1;
        exclusionComboBox.addItem(ExclusionType.GLOBAL.getValue());
        exclusionComboBox.addItem(ExclusionType.STRING_ENCRYPTION.getValue());
        exclusionComboBox.addItem(ExclusionType.INVOKEDYNAMIC.getValue());
        exclusionComboBox.addItem(ExclusionType.FLOW_OBFUSCATION.getValue());
        exclusionComboBox.addItem(ExclusionType.LINE_NUMBERS.getValue());
        exclusionComboBox.addItem(ExclusionType.LOCAL_VARIABLES.getValue());
        exclusionComboBox.addItem(ExclusionType.NUMBER_OBFUSCATION.getValue());
        exclusionComboBox.addItem(ExclusionType.HIDE_CODE.getValue());
        exclusionComboBox.addItem(ExclusionType.CRASHER.getValue());
        exclusionComboBox.addItem(ExclusionType.EXPIRATION.getValue());
        exclusionComboBox.addItem(ExclusionType.OPTIMIZER.getValue());
        exclusionComboBox.addItem(ExclusionType.SHRINKER.getValue());
        exclusionComboBox.addItem(ExclusionType.SHUFFLER.getValue());
        exclusionComboBox.addItem(ExclusionType.SOURCE_NAME.getValue());
        exclusionComboBox.addItem(ExclusionType.SOURCE_DEBUG.getValue());
        exclusionComboBox.addItem(ExclusionType.STRING_POOL.getValue());
        exclusionComboBox.addItem(ExclusionType.RENAMER.getValue());
        this.add(exclusionComboBox, gbc_exclusionComboBox);

        JTextField exclusionField = new JTextField();
        GridBagConstraints gbc_exclusionField = new GridBagConstraints();
        gbc_exclusionField.insets = new Insets(0, 0, 5, 5);
        gbc_exclusionField.fill = GridBagConstraints.HORIZONTAL;
        gbc_exclusionField.gridx = 1;
        gbc_exclusionField.gridy = 1;
        this.add(exclusionField, gbc_exclusionField);
        exclusionField.setColumns(10);

        JButton exclusionAddButton = new JButton("Add");
        GridBagConstraints gbc_exclusionAddButton = new GridBagConstraints();
        gbc_exclusionAddButton.insets = new Insets(0, 0, 5, 5);
        gbc_exclusionAddButton.gridx = 2;
        gbc_exclusionAddButton.gridy = 1;
        exclusionAddButton.addActionListener((e) -> {
            if (exclusionField.getText() != null && !exclusionField.getText().isEmpty()) {
                exclusions.addElement(exclusionComboBox.getItemAt(exclusionComboBox.getSelectedIndex()) + ": " + exclusionField.getText());
                exclusionField.setText(null);
            }
        });
        this.add(exclusionAddButton, gbc_exclusionAddButton);

        JButton exclusionRemoveButton = new JButton("Remove");
        GridBagConstraints gbc_exclusionRemoveButton = new GridBagConstraints();
        gbc_exclusionRemoveButton.insets = new Insets(0, 0, 5, 5);
        gbc_exclusionRemoveButton.gridx = 3;
        gbc_exclusionRemoveButton.gridy = 1;
        exclusionRemoveButton.addActionListener((e) -> {
            List<String> removeList = exclusionList.getSelectedValuesList();
            if (removeList.isEmpty())
                return;

            for (String s : removeList) {
                exclusions.removeElement(s);
            }
        });
        this.add(exclusionRemoveButton, gbc_exclusionRemoveButton);
    }

    public ExclusionManager getExclusions() {
        ExclusionManager manager = new ExclusionManager();
        for (int i = 0; i < exclusions.size(); i++) {
            manager.addExclusion(new Exclusion(exclusions.get(i)));
        }

        return manager;
    }

    public void setSettings(SessionInfo info) {
        exclusions.clear();

        if (info.getExclusions() != null) {
            ExclusionManager manager = info.getExclusions();
            manager.getExclusions().forEach(exclusion -> exclusions.addElement(exclusion.getExclusionType().getValue() + ": " + exclusion.getExclusion().pattern()));
        }
    }
}
