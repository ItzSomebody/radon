/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
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
import java.util.zip.ZipFile;
import me.itzsomebody.radon.cli.CommandArgumentsParser;
import me.itzsomebody.radon.config.ConfigurationParser;
import me.itzsomebody.radon.utils.IOUtils;
import me.itzsomebody.radon.utils.WatermarkUtils;

/**
 * Main class of obfuscator. \o/
 * TODO: Renamer transformer should correct strings used for reflection. (i.e. Class.forName("me.itzsomebody.Thing"))
 * <p>
 * TODO: Clean code up in general.
 *
 * @author ItzSomebody
 */
public class Main {
    public static final String VERSION = "2.0.0";
    public static final String CONTRIBUTORS = "ItzSomebody, x0ark, Col-E, Artel, kazigk, Olexorus and freeasbird";
    public static final String ATTRIBUTION = String.format("Radon is a free and open-source Java obfuscator " +
                    "with contributions from %s.\nVersion: %s\nWebsite: https://github.com/ItzSomebody/Radon",
            Main.CONTRIBUTORS, Main.VERSION);

    /**
     * Main method.
     *
     * @param args arguments from command line.
     */
    public static void main(String[] args) {
        Logger.stdOut("Version: " + VERSION);
        Logger.stdOut("Contributors: " + CONTRIBUTORS + "\n");

        // Registers the switches.
        CommandArgumentsParser.registerCommandSwitch("help", 0);
        CommandArgumentsParser.registerCommandSwitch("license", 0);
        CommandArgumentsParser.registerCommandSwitch("config", 1);
        CommandArgumentsParser.registerCommandSwitch("extract", 2);

        // Parse away!
        CommandArgumentsParser parser = new CommandArgumentsParser(args);

        // Switch handling.
        if (parser.containsSwitch("help")) {
            showHelpMenu();
        } else if (parser.containsSwitch("license")) {
            showLicense();
        } else if (parser.containsSwitch("config")) {
            File file = new File(parser.getSwitchArgs("config")[0]);
            ConfigurationParser config;
            try {
                config = new ConfigurationParser(new FileInputStream(file));
            } catch (FileNotFoundException exc) {
                Logger.stdErr(String.format("Configuration \"%s\" file not found", file.getName()));
                return;
            }

            // Parse the config and let's run Radon.
            Radon radon = new Radon(config.createObfuscatorConfiguration());
            radon.run();
        } else if (parser.containsSwitch("extract")) {
            // Watermark extraction.
            String[] switchArgs = parser.getSwitchArgs("extract");

            // Input file.
            File leaked = new File(switchArgs[0]);
            if (!leaked.exists()) {
                Logger.stdErr("Input file not found");
                return;
            }

            try {
                // Extract the ids and stick them into the console.
                Logger.stdOut(WatermarkUtils.extractIds(new ZipFile(leaked), switchArgs[1]));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
            showHelpMenu();
        }

        Logger.dumpLog();
    }

    private static String getProgramName() {
        return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    }

    /**
     * Prints help message into console.
     */
    private static void showHelpMenu() {
        String name = getProgramName();
        Logger.stdOut(String.format("CLI Usage:\t\t\tjava -jar %s --config example.config", name));
        Logger.stdOut(String.format("Help Menu:\t\t\tjava -jar %s --help", name));
        Logger.stdOut(String.format("License:\t\t\tjava -jar %s --license", name));
        Logger.stdOut(String.format("Watermark Extraction:\tjava -jar %s --extract Input.jar exampleKey", name));
    }

    /**
     * Spams the user's console full of legalese they don't care about whatsoever.
     */
    private static void showLicense() {
        System.out.println(new String(IOUtils.toByteArray(Main.class.getResourceAsStream("/license.txt"))));
    }
}
