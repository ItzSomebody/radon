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
import me.itzsomebody.radon.transformers.optimizers.Optimizer;
import me.itzsomebody.radon.transformers.optimizers.OptimizerDelegator;
import me.itzsomebody.radon.transformers.optimizers.OptimizerSetup;

public class OptimizationTab extends JPanel {
    private JCheckBox gotoGotoCheckBox;
    private JCheckBox gotoReturnCheckBox;
    private JCheckBox nopCheckBox;
    private JCheckBox optimizationEnabledCheckBox;

    public OptimizationTab() {
        GridBagLayout gbl_this = new GridBagLayout();
        gbl_this.columnWidths = new int[]{0, 0};
        gbl_this.rowHeights = new int[]{0, 0, 0};
        gbl_this.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_this.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        this.setBorder(new TitledBorder("Optimizer"));
        this.setLayout(gbl_this);

        JPanel optimizationSetupPanel = new JPanel();
        GridBagConstraints gbc_optimizationSetupPanel = new GridBagConstraints();
        gbc_optimizationSetupPanel.fill = GridBagConstraints.BOTH;
        gbc_optimizationSetupPanel.gridx = 0;
        gbc_optimizationSetupPanel.gridy = 1;
        this.add(optimizationSetupPanel, gbc_optimizationSetupPanel);
        GridBagLayout gbl_optimizationSetupPanel = new GridBagLayout();
        gbl_optimizationSetupPanel.columnWidths = new int[]{0, 0};
        gbl_optimizationSetupPanel.rowHeights = new int[]{0, 0, 0, 0};
        gbl_optimizationSetupPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl_optimizationSetupPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        optimizationSetupPanel.setBorder(new TitledBorder("Setup"));
        optimizationSetupPanel.setLayout(gbl_optimizationSetupPanel);

        gotoGotoCheckBox = new JCheckBox("Remove Goto-Goto Sequences");
        GridBagConstraints gbc_gotoGotoCheckBox = new GridBagConstraints();
        gbc_gotoGotoCheckBox.anchor = GridBagConstraints.WEST;
        gbc_gotoGotoCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_gotoGotoCheckBox.gridx = 0;
        gbc_gotoGotoCheckBox.gridy = 0;
        gotoGotoCheckBox.setEnabled(false);
        optimizationSetupPanel.add(gotoGotoCheckBox, gbc_gotoGotoCheckBox);

        gotoReturnCheckBox = new JCheckBox("Remove Goto-Return Sequences");
        GridBagConstraints gbc_gotoReturnCheckBox = new GridBagConstraints();
        gbc_gotoReturnCheckBox.anchor = GridBagConstraints.WEST;
        gbc_gotoReturnCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_gotoReturnCheckBox.gridx = 0;
        gbc_gotoReturnCheckBox.gridy = 1;
        gotoReturnCheckBox.setEnabled(false);
        optimizationSetupPanel.add(gotoReturnCheckBox, gbc_gotoReturnCheckBox);

        nopCheckBox = new JCheckBox("Remove Nop Instructions");
        GridBagConstraints gbc_nopCheckBox = new GridBagConstraints();
        gbc_nopCheckBox.anchor = GridBagConstraints.WEST;
        gbc_nopCheckBox.gridx = 0;
        gbc_nopCheckBox.gridy = 2;
        nopCheckBox.setEnabled(false);
        optimizationSetupPanel.add(nopCheckBox, gbc_nopCheckBox);

        optimizationEnabledCheckBox = new JCheckBox("Enabled");
        GridBagConstraints gbc_optimizationEnabledCheckBox = new GridBagConstraints();
        gbc_optimizationEnabledCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_optimizationEnabledCheckBox.anchor = GridBagConstraints.WEST;
        gbc_optimizationEnabledCheckBox.gridx = 0;
        gbc_optimizationEnabledCheckBox.gridy = 0;
        optimizationEnabledCheckBox.addActionListener((e) -> {
            boolean enable = optimizationEnabledCheckBox.isSelected();
            gotoGotoCheckBox.setEnabled(enable);
            gotoReturnCheckBox.setEnabled(enable);
            nopCheckBox.setEnabled(enable);
        });
        this.add(optimizationEnabledCheckBox, gbc_optimizationEnabledCheckBox);
    }

    public OptimizerDelegator getOptimizer() {
        return (optimizationEnabledCheckBox.isSelected()) ? new OptimizerDelegator(new OptimizerSetup(nopCheckBox.isSelected(), gotoGotoCheckBox.isSelected(), gotoReturnCheckBox.isSelected())) : null;
    }

    public void setSettings(SessionInfo info) {
        optimizationEnabledCheckBox.setSelected(false);
        nopCheckBox.setSelected(false);
        nopCheckBox.setEnabled(false);
        gotoReturnCheckBox.setSelected(false);
        gotoReturnCheckBox.setEnabled(false);
        gotoGotoCheckBox.setSelected(false);
        gotoGotoCheckBox.setEnabled(false);

        if (info.getTransformers() != null) {
            info.getTransformers().stream().filter(transformer -> transformer instanceof OptimizerDelegator).forEach(transformer -> {
                optimizationEnabledCheckBox.setEnabled(true);
                nopCheckBox.setEnabled(true);
                gotoReturnCheckBox.setEnabled(true);
                gotoGotoCheckBox.setEnabled(true);

                OptimizerSetup setup = ((OptimizerDelegator) transformer).getSetup();
                nopCheckBox.setSelected(setup.isNopRemoverEnabled());
                gotoReturnCheckBox.setSelected(setup.isGotoReturnEnabled());
                gotoGotoCheckBox.setSelected(setup.isGotoGotoEnabled());
            });
        }
    }
}
