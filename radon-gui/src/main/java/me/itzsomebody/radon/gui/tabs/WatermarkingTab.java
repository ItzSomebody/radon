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
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import me.itzsomebody.radon.SessionInfo;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.transformers.miscellaneous.watermarker.Watermarker;
import me.itzsomebody.radon.transformers.miscellaneous.watermarker.WatermarkerSetup;
import me.itzsomebody.radon.utils.WatermarkUtils;

/**
 * A {@link JPanel} which controls all of the watermarking settings in Radon.
 *
 * @author ItzSomebody
 */
public class WatermarkingTab extends JPanel {
    private JTextField watermarkMessageField;
    private JTextField watermarkKeyField;
    private JCheckBox watermarkerEnabledCheckBox;

    public WatermarkingTab() {
        GridBagLayout gbl_this = new GridBagLayout();
        gbl_this.columnWidths = new int[]{0, 0};
        gbl_this.rowHeights = new int[]{0, 0, 0, 0};
        gbl_this.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_this.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
        this.setBorder(new TitledBorder("Watermarker"));
        this.setLayout(gbl_this);

        JPanel watermarkerSetupPanel = new JPanel();
        GridBagConstraints gbc_watermarkerSetupPanel = new GridBagConstraints();
        gbc_watermarkerSetupPanel.insets = new Insets(0, 0, 5, 0);
        gbc_watermarkerSetupPanel.fill = GridBagConstraints.BOTH;
        gbc_watermarkerSetupPanel.gridx = 0;
        gbc_watermarkerSetupPanel.gridy = 1;
        this.add(watermarkerSetupPanel, gbc_watermarkerSetupPanel);
        watermarkerSetupPanel.setBorder(new TitledBorder("Setup"));
        GridBagLayout gbl_watermarkerSetupPanel = new GridBagLayout();
        gbl_watermarkerSetupPanel.columnWidths = new int[]{0, 0, 0};
        gbl_watermarkerSetupPanel.rowHeights = new int[]{0, 0, 0};
        gbl_watermarkerSetupPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gbl_watermarkerSetupPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        watermarkerSetupPanel.setLayout(gbl_watermarkerSetupPanel);

        JLabel watermarkMessageLabel = new JLabel("Message:");
        GridBagConstraints gbc_watermarkMessageLabel = new GridBagConstraints();
        gbc_watermarkMessageLabel.anchor = GridBagConstraints.EAST;
        gbc_watermarkMessageLabel.insets = new Insets(0, 5, 5, 5);
        gbc_watermarkMessageLabel.gridx = 0;
        gbc_watermarkMessageLabel.gridy = 0;
        watermarkerSetupPanel.add(watermarkMessageLabel, gbc_watermarkMessageLabel);

        watermarkMessageField = new JTextField();
        GridBagConstraints gbc_watermarkMessageField = new GridBagConstraints();
        gbc_watermarkMessageField.insets = new Insets(0, 0, 5, 5);
        gbc_watermarkMessageField.fill = GridBagConstraints.HORIZONTAL;
        gbc_watermarkMessageField.gridx = 1;
        gbc_watermarkMessageField.gridy = 0;
        watermarkMessageField.setEditable(false);
        watermarkerSetupPanel.add(watermarkMessageField, gbc_watermarkMessageField);
        watermarkMessageField.setColumns(10);

        JLabel watermarkKeyLabel = new JLabel("Key:");
        GridBagConstraints gbc_watermarkKeyLabel = new GridBagConstraints();
        gbc_watermarkKeyLabel.anchor = GridBagConstraints.EAST;
        gbc_watermarkKeyLabel.insets = new Insets(0, 0, 5, 5);
        gbc_watermarkKeyLabel.gridx = 0;
        gbc_watermarkKeyLabel.gridy = 1;
        watermarkerSetupPanel.add(watermarkKeyLabel, gbc_watermarkKeyLabel);

        watermarkKeyField = new JTextField();
        GridBagConstraints gbc_watermarkKeyField = new GridBagConstraints();
        gbc_watermarkKeyField.insets = new Insets(0, 0, 5, 5);
        gbc_watermarkKeyField.fill = GridBagConstraints.HORIZONTAL;
        gbc_watermarkKeyField.gridx = 1;
        gbc_watermarkKeyField.gridy = 1;
        watermarkKeyField.setEditable(false);
        watermarkerSetupPanel.add(watermarkKeyField, gbc_watermarkKeyField);
        watermarkKeyField.setColumns(10);

        JPanel watermarkerExtractor = new JPanel();
        GridBagConstraints gbc_watermarkerExtractor = new GridBagConstraints();
        gbc_watermarkerExtractor.fill = GridBagConstraints.BOTH;
        gbc_watermarkerExtractor.gridx = 0;
        gbc_watermarkerExtractor.gridy = 2;
        watermarkerExtractor.setBorder(new TitledBorder("Extractor"));
        this.add(watermarkerExtractor, gbc_watermarkerExtractor);
        GridBagLayout gbl_watermarkerExtractor = new GridBagLayout();
        gbl_watermarkerExtractor.columnWidths = new int[]{0, 0, 0, 0};
        gbl_watermarkerExtractor.rowHeights = new int[]{0, 0, 0, 0};
        gbl_watermarkerExtractor.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_watermarkerExtractor.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
        watermarkerExtractor.setLayout(gbl_watermarkerExtractor);

        JLabel watermarkExtractorInput = new JLabel("Input:");
        GridBagConstraints gbc_watermarkExtractorInput = new GridBagConstraints();
        gbc_watermarkExtractorInput.anchor = GridBagConstraints.EAST;
        gbc_watermarkExtractorInput.insets = new Insets(0, 5, 5, 5);
        gbc_watermarkExtractorInput.gridx = 0;
        gbc_watermarkExtractorInput.gridy = 0;
        watermarkerExtractor.add(watermarkExtractorInput, gbc_watermarkExtractorInput);

        JTextField watermarkExtractorInputField = new JTextField();
        GridBagConstraints gbc_watermarkExtractorInputField = new GridBagConstraints();
        gbc_watermarkExtractorInputField.insets = new Insets(0, 0, 5, 5);
        gbc_watermarkExtractorInputField.fill = GridBagConstraints.HORIZONTAL;
        gbc_watermarkExtractorInputField.gridx = 1;
        gbc_watermarkExtractorInputField.gridy = 0;
        watermarkerExtractor.add(watermarkExtractorInputField, gbc_watermarkExtractorInputField);
        watermarkExtractorInputField.setColumns(10);

        JButton watermarkExtractorInputButton = new JButton("Select");
        GridBagConstraints gbc_watermarkExtractorInputButton = new GridBagConstraints();
        gbc_watermarkExtractorInputButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_watermarkExtractorInputButton.insets = new Insets(0, 0, 5, 5);
        gbc_watermarkExtractorInputButton.gridx = 2;
        gbc_watermarkExtractorInputButton.gridy = 0;
        watermarkExtractorInputButton.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = chooser.showOpenDialog(this);
            if (result == 0) {
                SwingUtilities.invokeLater(() ->
                        watermarkExtractorInputField.setText(chooser.getSelectedFile().getAbsolutePath()));
            }
        });
        watermarkerExtractor.add(watermarkExtractorInputButton, gbc_watermarkExtractorInputButton);

        JLabel watermarkExtractorKeyLabel = new JLabel("Key:");
        GridBagConstraints gbc_watermarkExtractorKeyLabel = new GridBagConstraints();
        gbc_watermarkExtractorKeyLabel.anchor = GridBagConstraints.EAST;
        gbc_watermarkExtractorKeyLabel.insets = new Insets(0, 0, 5, 5);
        gbc_watermarkExtractorKeyLabel.gridx = 0;
        gbc_watermarkExtractorKeyLabel.gridy = 1;
        watermarkerExtractor.add(watermarkExtractorKeyLabel, gbc_watermarkExtractorKeyLabel);

        JTextField watermarkExtractorKeyField = new JTextField();
        GridBagConstraints gbc_watermarkExtractorKeyField = new GridBagConstraints();
        gbc_watermarkExtractorKeyField.insets = new Insets(0, 0, 5, 5);
        gbc_watermarkExtractorKeyField.fill = GridBagConstraints.HORIZONTAL;
        gbc_watermarkExtractorKeyField.gridx = 1;
        gbc_watermarkExtractorKeyField.gridy = 1;
        watermarkerExtractor.add(watermarkExtractorKeyField, gbc_watermarkExtractorKeyField);
        watermarkExtractorKeyField.setColumns(10);

        JScrollPane watermarkExtractorScrollPane = new JScrollPane();
        GridBagConstraints gbc_watermarkExtractorScrollPane = new GridBagConstraints();
        gbc_watermarkExtractorScrollPane.gridwidth = 3;
        gbc_watermarkExtractorScrollPane.insets = new Insets(0, 5, 5, 5);
        gbc_watermarkExtractorScrollPane.fill = GridBagConstraints.BOTH;
        gbc_watermarkExtractorScrollPane.gridx = 0;
        gbc_watermarkExtractorScrollPane.gridy = 2;
        watermarkerExtractor.add(watermarkExtractorScrollPane, gbc_watermarkExtractorScrollPane);

        DefaultListModel<String> extractionList = new DefaultListModel<>();
        JList<String> watermarkExtractorList = new JList<>(extractionList);
        watermarkExtractorScrollPane.setViewportView(watermarkExtractorList);

        JButton watermarkExtractorButton = new JButton("Extract");
        GridBagConstraints gbc_watermarkExtractorButton = new GridBagConstraints();
        gbc_watermarkExtractorButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_watermarkExtractorButton.insets = new Insets(0, 0, 5, 5);
        gbc_watermarkExtractorButton.gridx = 2;
        gbc_watermarkExtractorButton.gridy = 1;
        watermarkExtractorButton.addActionListener((e) -> {
            extractionList.clear();
            File file = new File(watermarkExtractorInputField.getText());
            if (!file.exists()) {
                throw new RadonException(String.format("Could not find input file %s.",
                        watermarkExtractorInputField.getText()));
            }

            try {
                ZipFile zipFile = new ZipFile(file);
                List<String> ids = WatermarkUtils.extractIds(zipFile, watermarkExtractorKeyField.getText());

                for (String id : ids) {
                    extractionList.addElement(id);
                }
            } catch (ZipException ze) {
                ze.printStackTrace();
                throw new RadonException("Could not load input file as a zip.");
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RadonException();
            }
        });
        watermarkerExtractor.add(watermarkExtractorButton, gbc_watermarkExtractorButton);

        watermarkerEnabledCheckBox = new JCheckBox("Enabled");
        GridBagConstraints gbc_watermarkerEnabledCheckBox = new GridBagConstraints();
        gbc_watermarkerEnabledCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_watermarkerEnabledCheckBox.anchor = GridBagConstraints.WEST;
        gbc_watermarkerEnabledCheckBox.gridx = 0;
        gbc_watermarkerEnabledCheckBox.gridy = 0;
        watermarkerEnabledCheckBox.addActionListener((e) -> {
            boolean enable = watermarkerEnabledCheckBox.isSelected();

            watermarkMessageField.setEditable(enable);
            watermarkKeyField.setEditable(enable);
        });
        this.add(watermarkerEnabledCheckBox, gbc_watermarkerEnabledCheckBox);
    }

    /**
     * Creates an {@link Watermarker} transformer setup accordingly to the information provided in this
     * {@link WatermarkingTab}.
     *
     * @return an {@link Watermarker} transformer setup accordingly to the information provided in this
     * {@link WatermarkingTab}.
     */
    public Watermarker getWatermarker() {
        return (watermarkerEnabledCheckBox.isSelected())
                ? new Watermarker(new WatermarkerSetup(watermarkMessageField.getText(),
                watermarkKeyField.getText())) : null;
    }

    /**
     * Sets the tab settings accordingly with the provided {@link SessionInfo}.
     *
     * @param info the {@link SessionInfo} used to determine the tab setup.
     */
    public void setSettings(SessionInfo info) {
        watermarkerEnabledCheckBox.setSelected(false);
        watermarkMessageField.setText(null);
        watermarkMessageField.setEditable(false);
        watermarkKeyField.setText(null);
        watermarkKeyField.setEditable(false);

        if (info.getTransformers() != null) {
            info.getTransformers().stream().filter(transformer ->
                    transformer instanceof Watermarker).forEach(transformer -> {
                watermarkerEnabledCheckBox.setSelected(true);
                watermarkMessageField.setEditable(true);
                watermarkKeyField.setEditable(true);

                WatermarkerSetup setup = ((Watermarker) transformer).getSetup();

                watermarkMessageField.setText(setup.getMessage());
                watermarkKeyField.setText(setup.getKey());
            });
        }
    }
}
