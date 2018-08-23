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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.*;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.Radon;
import me.itzsomebody.radon.SessionInfo;
import me.itzsomebody.radon.config.ConfigurationParser;
import me.itzsomebody.radon.config.ConfigurationWriter;
import me.itzsomebody.radon.exceptions.ConfigurationParseException;
import me.itzsomebody.radon.gui.tabs.ConsoleTab;
import me.itzsomebody.radon.gui.tabs.ExclusionsTab;
import me.itzsomebody.radon.gui.tabs.InputOutputTab;
import me.itzsomebody.radon.gui.tabs.MiscellaneousTab;
import me.itzsomebody.radon.gui.tabs.ObfuscationTab;
import me.itzsomebody.radon.gui.tabs.OptimizationTab;
import me.itzsomebody.radon.gui.tabs.ShrinkingTab;
import me.itzsomebody.radon.gui.tabs.WatermarkingTab;
import me.itzsomebody.radon.transformers.Transformer;

class RadonGUI extends JFrame {
    RadonGUI() {
        setTitle(Main.PREFIX + " - " + Main.VERSION);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignored
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(700, 570);
        setLocationRelativeTo(null);

        JTabbedPane tabsPane = new JTabbedPane(JTabbedPane.LEFT);
        getContentPane().add(tabsPane, BorderLayout.CENTER);

        InputOutputTab inputOutputTab = new InputOutputTab();
        tabsPane.addTab("Input-Output", null, inputOutputTab, null);

        ObfuscationTab obfuscationPanel = new ObfuscationTab();
        tabsPane.addTab("Obfuscation", null, obfuscationPanel, null);

        OptimizationTab optimizationPanel = new OptimizationTab();
        tabsPane.addTab("Optimization", null, optimizationPanel, null);

        ShrinkingTab shrinkingPanel = new ShrinkingTab();
        tabsPane.addTab("Shrinking", null, shrinkingPanel, null);

        WatermarkingTab watermarkingPanel = new WatermarkingTab();
        tabsPane.addTab("Watermarking", null, watermarkingPanel, null);

        MiscellaneousTab miscPanel = new MiscellaneousTab();
        tabsPane.addTab("Miscellaneous", null, miscPanel, null);

        ExclusionsTab exclusionPanel = new ExclusionsTab();
        tabsPane.addTab("Exclusions", null, exclusionPanel, null);

        ConsoleTab consolePanel = new ConsoleTab();
        tabsPane.addTab("Console", null, consolePanel, null);

        JPanel bottomToolBar = new JPanel();
        getContentPane().add(bottomToolBar, BorderLayout.SOUTH);
        bottomToolBar.setLayout(new BorderLayout(0, 0));

        JPanel toolBarPanel = new JPanel();
        bottomToolBar.add(toolBarPanel, BorderLayout.EAST);

        JButton loadConfigButton = new JButton("Load Configuration");
        loadConfigButton.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = chooser.showOpenDialog(this);
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        InputStream inputStream;
                        try {
                            inputStream = new FileInputStream(chooser.getSelectedFile());
                        } catch (FileNotFoundException exception) {
                            exception.printStackTrace();
                            throw new ConfigurationParseException("Could not find configuration file");
                        }

                        SessionInfo info = new ConfigurationParser(inputStream).createSessionFromConfig();
                        inputOutputTab.setSettings(info);
                        obfuscationPanel.setSettings(info);
                        optimizationPanel.setSettings(info);
                        shrinkingPanel.setSettings(info);
                        watermarkingPanel.setSettings(info);
                        miscPanel.setSettings(info);
                        exclusionPanel.setSettings(info);
                    } catch (Throwable t) {
                        JOptionPane.showMessageDialog(null, "Error while parsing config, check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
                        t.printStackTrace();
                    }
                });
            }
        });
        toolBarPanel.add(loadConfigButton);

        JButton saveConfigButton = new JButton("Save Configuration");
        saveConfigButton.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = chooser.showOpenDialog(this);
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        SessionInfo sessionInfo = new SessionInfo();

                        sessionInfo.setInput(new File(inputOutputTab.getInputPath()));
                        sessionInfo.setOutput(new File(inputOutputTab.getOutputPath()));
                        sessionInfo.setLibraries(inputOutputTab.getLibraries());

                        ArrayList<Transformer> transformers = new ArrayList<>();
                        transformers.add(shrinkingPanel.getShrinker());
                        transformers.add(optimizationPanel.getOptimizer());
                        transformers.add(obfuscationPanel.getRenamer());
                        transformers.add(obfuscationPanel.getNumberObfuscation());
                        transformers.add(obfuscationPanel.getInvokeDynamic());
                        transformers.add(obfuscationPanel.getStringEncryption());
                        transformers.add(obfuscationPanel.getStringPool());
                        transformers.add(obfuscationPanel.getFlowObfuscation());
                        transformers.add(obfuscationPanel.getShuffler());
                        transformers.add(obfuscationPanel.getLocalVarObfuscation());
                        transformers.add(obfuscationPanel.getLineNumberObfuscation());
                        transformers.add(obfuscationPanel.getSourceNameObfuscation());
                        transformers.add(obfuscationPanel.getSourceDebugObfuscation());
                        transformers.add(obfuscationPanel.getCrasher());
                        transformers.add(obfuscationPanel.getHideCodeObfuscation());
                        transformers.add(miscPanel.getExpiration());
                        transformers.add(watermarkingPanel.getWatermarker());
                        sessionInfo.setTransformers(transformers);

                        sessionInfo.setExclusions(exclusionPanel.getExclusions());

                        sessionInfo.setTrashClasses(miscPanel.getTrashClasses());
                        sessionInfo.setDictionaryType(miscPanel.getDictionary());

                        ConfigurationWriter writer = new ConfigurationWriter(sessionInfo);
                        File file = chooser.getSelectedFile();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                        bw.write(writer.dump());
                        bw.close();
                    } catch (Throwable t) {
                        JOptionPane.showMessageDialog(null, "Error while creating config, check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });
        toolBarPanel.add(saveConfigButton);

        JButton processButton = new JButton("Process");
        processButton.addActionListener((e) -> {
            consolePanel.resetConsole();
            processButton.setText("Processing...");
            processButton.setEnabled(false);

            SwingWorker sw = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    try {
                        SessionInfo sessionInfo = new SessionInfo();

                        sessionInfo.setInput(new File(inputOutputTab.getInputPath()));
                        sessionInfo.setOutput(new File(inputOutputTab.getOutputPath()));
                        sessionInfo.setLibraries(inputOutputTab.getLibraries());

                        ArrayList<Transformer> transformers = new ArrayList<>();
                        transformers.add(shrinkingPanel.getShrinker());
                        transformers.add(optimizationPanel.getOptimizer());
                        transformers.add(obfuscationPanel.getRenamer());
                        transformers.add(obfuscationPanel.getNumberObfuscation());
                        transformers.add(obfuscationPanel.getInvokeDynamic());
                        transformers.add(obfuscationPanel.getStringEncryption());
                        transformers.add(obfuscationPanel.getStringPool());
                        transformers.add(obfuscationPanel.getFlowObfuscation());
                        transformers.add(obfuscationPanel.getShuffler());
                        transformers.add(obfuscationPanel.getLocalVarObfuscation());
                        transformers.add(obfuscationPanel.getLineNumberObfuscation());
                        transformers.add(obfuscationPanel.getSourceNameObfuscation());
                        transformers.add(obfuscationPanel.getSourceDebugObfuscation());
                        transformers.add(obfuscationPanel.getCrasher());
                        transformers.add(obfuscationPanel.getHideCodeObfuscation());
                        transformers.add(miscPanel.getExpiration());
                        transformers.add(watermarkingPanel.getWatermarker());
                        sessionInfo.setTransformers(transformers);

                        sessionInfo.setExclusions(exclusionPanel.getExclusions());

                        sessionInfo.setTrashClasses(miscPanel.getTrashClasses());
                        sessionInfo.setDictionaryType(miscPanel.getDictionary());

                        Radon radon = new Radon(sessionInfo);
                        radon.partyTime();

                        JOptionPane.showMessageDialog(null, "Processed successfully.", "Done", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error happened while processing, check the console for details.", "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        processButton.setText("Process");
                        processButton.setEnabled(true);
                    }
                    return null;
                }
            };

            sw.execute();
        });
        toolBarPanel.add(processButton);
        setVisible(true);
    }
}
