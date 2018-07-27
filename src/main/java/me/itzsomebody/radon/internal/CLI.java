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

package me.itzsomebody.radon.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import me.itzsomebody.radon.Radon;
import me.itzsomebody.radon.config.Config;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.WatermarkUtils;

/**
 * CLI class to manage command line usage
 *
 * @author ItzSomebody
 */
public class CLI {
    /**
     * Args passed down from {@link Radon#main(String[])}.
     */
    private String[] args;

    /**
     * Constructor to create a new {@link CLI} object.
     *
     * @param args array of {@link String}s from command line arguments.
     */
    public CLI(String[] args) {
        this.args = args;
        this.startTheParty();
    }

    /**
     * Parses {@link CLI#args}.
     */
    private void startTheParty() {
        if (this.args.length == 1) {
            switch (this.args[0].toLowerCase()) {
                case "--version":
                case "-version":
                case "version":
                case "/version":
                    break;
                case "--help":
                case "-help":
                case "help":
                case "/help":
                    helpMsg();
                    break;
                default:
                    usageMsg();
            }
        } else if (this.args.length == 2) {
            switch (this.args[0].toLowerCase()) {
                case "--config":
                case "-config":
                case "config":
                case "/config":
                    File file = new File(this.args[1]);
                    Config config;
                    try {
                        config = new Config(new FileInputStream(file));
                    } catch (FileNotFoundException exc) {
                        LoggerUtils.stdOut("Config file not found");
                        return;
                    }
                    Bootstrap bootstrap = new Bootstrap(config);
                    try {
                        bootstrap.startTheParty(true);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    break;
                default:
                    usageMsg();
                    break;
            }
        } else if (this.args.length == 3) {
            switch (this.args[0].toLowerCase()) {
                case "--extract":
                case "-extract":
                case "extract":
                case "/extract":
                    File leaked = new File(this.args[1]);
                    if (!leaked.exists()) {
                        LoggerUtils.stdOut("Input file not found");
                    }

                    try {
                        List<String> ids = WatermarkUtils
                                .extractWatermark(leaked, this.args[2]);
                        for (String id : ids) {
                            LoggerUtils.stdOut(id);
                        }
                        return;
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    break;
                default:
                    usageMsg();
                    break;
            }
        } else {
            usageMsg();
        }
    }

    /**
     * Prints usage message into console.
     */
    private static void usageMsg() {
        LoggerUtils.stdOut("Usage: java -jar Radon.jar --config example" +
                ".config");
        LoggerUtils.stdOut("USage: java -jar Radon.jar --help");
        LoggerUtils.stdOut("Usage: java -jar Radon.jar");
    }

    /**
     * Prints help message into console.
     */
    private static void helpMsg() {
        LoggerUtils.stdOut("CLI Usage:\t\tjava -jar Radon.jar --config " +
                "example.config");
        LoggerUtils.stdOut("Credits:\t\tjava -jar Radon.jar --version");
        LoggerUtils.stdOut("Help Menu:\t\tjava -jar Radon.jar --help");
        LoggerUtils.stdOut("Watermark Extraction:\tjava -jar Radon.jar " +
                "--extract Input.jar exampleKey");
        LoggerUtils.stdOut("MainGUI Usage:\t\tjava -jar Radon.jar");
    }
}
