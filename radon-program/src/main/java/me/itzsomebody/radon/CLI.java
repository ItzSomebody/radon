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

package me.itzsomebody.radon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.zip.ZipFile;
import me.itzsomebody.radon.config.ConfigurationParser;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.WatermarkUtils;

/**
 * CLI class to manage command line usage
 *
 * @author ItzSomebody
 */
class CLI {
    /**
     * Args passed down from {@link Main#main(String[])}.
     */
    private final String[] args;

    /**
     * Constructor to create a new {@link CLI} object.
     *
     * @param args array of {@link String}s from command line arguments.
     */
    CLI(String[] args) {
        this.args = args;
        this.startTheParty();
    }

    /**
     * Parses {@link CLI#args}.
     */
    private void startTheParty() {
        String firstArg = args.length > 0 ? this.args[0].toLowerCase() : null;

        switch (this.args.length) {
            case 1:
                if (firstArg.contains("help")) {
                    helpMsg();
                } else {
                    usageMsg();
                }
                break;

            case 2:
                if (firstArg.contains("config")) {

                    File file = new File(this.args[1]);
                    ConfigurationParser config;
                    try {
                        config = new ConfigurationParser(new FileInputStream(file));
                    } catch (FileNotFoundException exc) {
                        LoggerUtils.stdOut("Configuration file not found");
                        break;
                    }

                    Radon radon = new Radon(config.createSessionFromConfig());
                    radon.partyTime();

                } else {
                    usageMsg();
                }
                break;

            case 3:
                if (firstArg.contains("extract")) {

                    File leaked = new File(this.args[1]);
                    if (!leaked.exists()) {
                        LoggerUtils.stdOut("Input file not found");
                    }

                    try {
                        List<String> ids = WatermarkUtils.extractIds(new ZipFile(leaked), this.args[2]);
                        ids.forEach(LoggerUtils::stdOut);
                        return;
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }

                } else {
                    usageMsg();
                }
                break;

            default:
                usageMsg();
        }
    }

    /**
     * Prints usage message into console.
     */
    private static void usageMsg() {
        LoggerUtils.stdOut("Usage: java -jar Radon-Program.jar --config example.config");
        LoggerUtils.stdOut("Usage: java -jar Radon-Program.jar --help");
        LoggerUtils.stdOut("Usage: java -jar Radon-Program.jar");
    }

    /**
     * Prints help message into console.
     */
    private static void helpMsg() {
        LoggerUtils.stdOut("CLI Usage:\t\t\t\tjava -jar Radon-Program.jar --config example.config");
        LoggerUtils.stdOut("Help Menu:\t\t\t\tjava -jar Radon-Program.jar --help");
        LoggerUtils.stdOut("Watermark Extraction:\tjava -jar Radon-Program.jar --extract Input.jar exampleKey");
        LoggerUtils.stdOut("Radon GUI Usage:\t\tjava -jar Radon-GUI.jar");
    }
}
