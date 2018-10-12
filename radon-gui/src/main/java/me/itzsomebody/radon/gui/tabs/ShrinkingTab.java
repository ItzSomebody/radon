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
import javax.swing.*;
import javax.swing.border.TitledBorder;
import me.itzsomebody.radon.SessionInfo;
import me.itzsomebody.radon.transformers.shrinkers.ShrinkerDelegator;
import me.itzsomebody.radon.transformers.shrinkers.ShrinkerSetup;

/**
 * A {@link JPanel} which controls all of the shrinking settings in Radon.
 *
 * @author ItzSomebody
 */
public class ShrinkingTab extends JPanel {
    private JCheckBox attributesCheckBox;
    private JCheckBox debugInfoCheckBox;
    private JCheckBox invisibleAnnotationsCheckBox;
    private JCheckBox visibleAnnotationsCheckBox;
    private JCheckBox shrinkerEnabledCheckBox;

    public ShrinkingTab() {
        GridBagLayout gbl_this = new GridBagLayout();
        gbl_this.columnWidths = new int[]{0, 0};
        gbl_this.rowHeights = new int[]{0, 0, 0};
        gbl_this.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_this.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        this.setBorder(new TitledBorder("Shrinker"));
        this.setLayout(gbl_this);

        JPanel shrinkerSetupPanel = new JPanel();
        GridBagConstraints gbc_shrinkerSetupPanel = new GridBagConstraints();
        gbc_shrinkerSetupPanel.fill = GridBagConstraints.BOTH;
        gbc_shrinkerSetupPanel.gridx = 0;
        gbc_shrinkerSetupPanel.gridy = 1;
        this.add(shrinkerSetupPanel, gbc_shrinkerSetupPanel);
        GridBagLayout gbl_shrinkerSetupPanel = new GridBagLayout();
        gbl_shrinkerSetupPanel.columnWidths = new int[]{0, 0};
        gbl_shrinkerSetupPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
        gbl_shrinkerSetupPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl_shrinkerSetupPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        shrinkerSetupPanel.setBorder(new TitledBorder("Setup"));
        shrinkerSetupPanel.setLayout(gbl_shrinkerSetupPanel);

        attributesCheckBox = new JCheckBox("Remove Attributes");
        GridBagConstraints gbc_attributesCheckBox = new GridBagConstraints();
        gbc_attributesCheckBox.anchor = GridBagConstraints.WEST;
        gbc_attributesCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_attributesCheckBox.gridx = 0;
        gbc_attributesCheckBox.gridy = 0;
        attributesCheckBox.setEnabled(false);
        shrinkerSetupPanel.add(attributesCheckBox, gbc_attributesCheckBox);

        debugInfoCheckBox = new JCheckBox("Remove Unneeded Debugging Information");
        GridBagConstraints gbc_debugInfoCheckBox = new GridBagConstraints();
        gbc_debugInfoCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_debugInfoCheckBox.gridx = 0;
        gbc_debugInfoCheckBox.gridy = 1;
        debugInfoCheckBox.setEnabled(false);
        debugInfoCheckBox.setToolTipText("Removes innerclasses, outerclass, outermethod, etc.");
        shrinkerSetupPanel.add(debugInfoCheckBox, gbc_debugInfoCheckBox);

        invisibleAnnotationsCheckBox = new JCheckBox("Remove Invisible Annotations");
        GridBagConstraints gbc_invisibleAnnotationsCheckBox = new GridBagConstraints();
        gbc_invisibleAnnotationsCheckBox.anchor = GridBagConstraints.WEST;
        gbc_invisibleAnnotationsCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_invisibleAnnotationsCheckBox.gridx = 0;
        gbc_invisibleAnnotationsCheckBox.gridy = 2;
        invisibleAnnotationsCheckBox.setEnabled(false);
        shrinkerSetupPanel.add(invisibleAnnotationsCheckBox, gbc_invisibleAnnotationsCheckBox);

        visibleAnnotationsCheckBox = new JCheckBox("Remove Visible Annotations");
        GridBagConstraints gbc_visibleAnnotationsCheckBox = new GridBagConstraints();
        gbc_visibleAnnotationsCheckBox.anchor = GridBagConstraints.WEST;
        gbc_visibleAnnotationsCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_visibleAnnotationsCheckBox.gridx = 0;
        gbc_visibleAnnotationsCheckBox.gridy = 3;
        visibleAnnotationsCheckBox.setEnabled(false);
        shrinkerSetupPanel.add(visibleAnnotationsCheckBox, gbc_visibleAnnotationsCheckBox);

        shrinkerEnabledCheckBox = new JCheckBox("Enabled");
        GridBagConstraints gbc_shrinkerEnabledCheckBox = new GridBagConstraints();
        gbc_shrinkerEnabledCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_shrinkerEnabledCheckBox.anchor = GridBagConstraints.WEST;
        gbc_shrinkerEnabledCheckBox.gridx = 0;
        gbc_shrinkerEnabledCheckBox.gridy = 0;
        shrinkerEnabledCheckBox.addActionListener((e) -> {
            boolean enable = shrinkerEnabledCheckBox.isSelected();
            attributesCheckBox.setEnabled(enable);
            debugInfoCheckBox.setEnabled(enable);
            invisibleAnnotationsCheckBox.setEnabled(enable);
            visibleAnnotationsCheckBox.setEnabled(enable);
        });
        this.add(shrinkerEnabledCheckBox, gbc_shrinkerEnabledCheckBox);
    }

    /**
     * Returns an {@link ShrinkerDelegator} setup accordingly to this {@link ShrinkingTab}.
     *
     * @return an {@link ShrinkerDelegator} setup accordingly to this {@link ShrinkingTab}.
     */
    public ShrinkerDelegator getShrinker() {
        return (shrinkerEnabledCheckBox.isSelected()) ?
                new ShrinkerDelegator(new ShrinkerSetup(visibleAnnotationsCheckBox.isSelected(),
                        invisibleAnnotationsCheckBox.isSelected(), attributesCheckBox.isSelected(),
                        debugInfoCheckBox.isSelected())) : null;
    }

    /**
     * Sets the tab settings accordingly with the provided {@link SessionInfo}.
     *
     * @param info the {@link SessionInfo} used to determine the tab setup.
     */
    public void setSettings(SessionInfo info) {
        shrinkerEnabledCheckBox.setSelected(false);
        attributesCheckBox.setSelected(false);
        attributesCheckBox.setEnabled(false);
        debugInfoCheckBox.setSelected(false);
        debugInfoCheckBox.setEnabled(false);
        invisibleAnnotationsCheckBox.setSelected(false);
        invisibleAnnotationsCheckBox.setEnabled(false);
        visibleAnnotationsCheckBox.setSelected(false);
        visibleAnnotationsCheckBox.setEnabled(false);

        if (info.getTransformers() != null) {
            info.getTransformers().stream().filter(transformer -> transformer instanceof ShrinkerDelegator)
                    .forEach(transformer -> {
                        shrinkerEnabledCheckBox.setSelected(true);
                        attributesCheckBox.setEnabled(true);
                        debugInfoCheckBox.setEnabled(true);
                        invisibleAnnotationsCheckBox.setEnabled(true);
                        visibleAnnotationsCheckBox.setEnabled(true);

                        ShrinkerSetup setup = ((ShrinkerDelegator) transformer).getSetup();
                        attributesCheckBox.setSelected(setup.isRemoveAttributes());
                        debugInfoCheckBox.setSelected(setup.isRemoveDebug());
                        invisibleAnnotationsCheckBox.setSelected(setup.isRemoveInvisibleAnnotations());
                        visibleAnnotationsCheckBox.setSelected(setup.isRemoveVisibleAnnotations());
                    });
        }
    }
}
