/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2020 ItzSomebody
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

package xyz.itzsomebody.radon;

import xyz.itzsomebody.radon.cli.CmdArgsParser;
import xyz.itzsomebody.radon.config.ConfigurationParser;
import xyz.itzsomebody.radon.config.ObfConfig;
import xyz.itzsomebody.radon.exceptions.FatalRadonException;
import xyz.itzsomebody.radon.exceptions.PreventableRadonException;
import xyz.itzsomebody.radon.transformers.misc.Watermarker;
import xyz.itzsomebody.radon.utils.IOUtils;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipFile;

/**
 * Entry point for the Radon Java bytecode obfuscator.
 *
 * @author itzsomebody
 */
public class RadonMain {
    /**
     * Underlying implementation of {@link RadonMain#bootstrap(boolean)}.
     *
     * @param cliMode Determines if the obfuscator should initialize {@link CmdArgsParser}.
     */
    @SuppressWarnings("SameParameterValue")
    private static void bootstrap0(final boolean cliMode) {
        // RadonLogger
        try {
            // Throws from static initializer if some random thing should go rip
            RadonLogger.info("Finished setting up RadonLogger");
        } catch (Throwable t) {
            System.out.println("Some random error happened while bootstrapping logger D:");
            t.printStackTrace(System.out);
            System.exit(-1);
        }

        // CLI stuff
        // For console-application usage
        if (cliMode) {
            CmdArgsParser.registerSwitch("help", 0);
            CmdArgsParser.registerSwitch("license", 0); // not that anybody cares abt this one
            CmdArgsParser.registerSwitch("config", 1);
            CmdArgsParser.registerSwitch("extract", 2);
            RadonLogger.info("Finished setting up command line parser");
        }
    }

    /**
     * Initializes the {@link RadonLogger}. Also initializes {@link CmdArgsParser} if the obfuscator is being run
     * from the console.
     *
     * @param cliMode Determines if the obfuscator should initialize {@link CmdArgsParser}.
     */
    public static void bootstrap(boolean cliMode) {
        bootstrap0(cliMode);
        RadonLogger.info("Finished bootstrap phase");
        RadonLogger.info("Radon: A free and open-source experimental JVM bytecode obfuscator");
        RadonLogger.info(String.format("Version: %s (commit hash: %s)", RadonConstants.VERSION, RadonConstants.GIT_HASH));
        RadonLogger.info("Repository: https://github.com/ItzSomebody/radon");
    }

    private static int cliThing(String[] args) {
        // Parse all the args off the command line
        var parser = new CmdArgsParser();
        parser.parse(args);

        // Do stuff based on whatever was thrown in
        if (parser.containsSwitch("help")) {
            var programName = new File(RadonMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();

            RadonLogger.info(String.format("Extractor: %5s java -jar %s --extract example.jar", "", programName));
            RadonLogger.info(String.format("CLI Usage: %5s java -jar %s --config example.yml", "", programName));
            RadonLogger.info(String.format("Help Menu: %5s java -jar %s --config example.yml", "", programName));
            RadonLogger.info(String.format("License: %5s java -jar %s --config example.yml", "", programName));
        } else if (parser.containsSwitch("license")) {
            try {
                RadonLogger.info(new String(IOUtils.toByteArray(RadonMain.class.getResourceAsStream("/radon-license.txt")), StandardCharsets.UTF_8));
            } catch (IOException ioe) {
                RadonLogger.severe("Unable to load the license file");
                ioe.printStackTrace(System.out);
            }
        } else if (parser.containsSwitch("config")) {
            var configPath = parser.getArgsFor("config")[0];
            var configFile = new File(configPath);
            if (!configFile.exists()) {
                RadonLogger.severe(String.format("Could not find specified config file \"%s\"", configFile.getAbsolutePath()));
            }
            if (!configFile.canRead()) {
                RadonLogger.severe(String.format("Cannot not read specified config file \"%s\"", configFile.getAbsolutePath()));
            }
            if (!configFile.isFile()) {
                RadonLogger.severe(String.format("Specified config file \"%s\" is not a file", configFile.getAbsolutePath()));
            }

            ConfigurationParser config;
            try {
                config = new ConfigurationParser(new FileInputStream(configFile));
            } catch (FileNotFoundException e) {
                RadonLogger.severe("Unknown IO error happened: " + e.getMessage());

                if (RadonConstants.VERBOSE) {
                    e.printStackTrace();
                }
                return -1;
            }

            try {
                var radon = new Radon(config.parseConfig());
                radon.run();
            } catch (FatalRadonException e) {
                RadonLogger.severe("A fatal exception was thrown: " + e.getMessage());

                if (RadonConstants.VERBOSE) {
                    e.printStackTrace(System.out);
                }
                return -1;
            } catch (PreventableRadonException e) {
                RadonLogger.severe("A preventable exception was thrown: " + e.getMessage());

                if (RadonConstants.VERBOSE) {
                    e.printStackTrace(System.out);
                }
                return 1;
            } catch (Throwable t) {
                RadonLogger.severe("An unknown error was throw: " + t.getMessage());

                if (RadonConstants.VERBOSE) {
                    t.printStackTrace(System.out);
                }
                return -1;
            }
        } else if (parser.containsSwitch("extract")) {
            // At this point I was too lazy to write proper code -- I don't think anyone uses this anyways lmao
            var switchArgs = parser.getArgsFor("extract");

            File leaked = new File(switchArgs[0]);
            if (!leaked.exists()) {
                RadonLogger.severe("Input file not found");
                return -1;
            }

            try {
                var extractor = new Watermarker.Extractor(new ZipFile(leaked), switchArgs[1]);
                RadonLogger.info(extractor.extractId());
            } catch (FatalRadonException e) {
                RadonLogger.severe("A fatal exception was thrown: " + e.getMessage());

                if (RadonConstants.VERBOSE) {
                    e.printStackTrace(System.out);
                }
                return -1;
            } catch (PreventableRadonException e) {
                RadonLogger.severe("A preventable exception was thrown: " + e.getMessage());

                if (RadonConstants.VERBOSE) {
                    e.printStackTrace(System.out);
                }
                return 1;
            } catch (Throwable t) {
                RadonLogger.severe("An unknown error was throw: " + t.getMessage());

                if (RadonConstants.VERBOSE) {
                    t.printStackTrace(System.out);
                }
                return -1;
            }
        } else {
            RadonLogger.info("Unknown operation: perhaps try viewing the results of --help?");
        }

        // Thank the Lord
        RadonLogger.info("Exiting gracefully");
        return 0;
    }

    /**
     * Entry point to the obfuscator.
     */
    public static void main(String[] args) {
        // Bootstrap to init stuffs
        bootstrap(true);

        // Run the stuff
        System.exit(cliThing(args));
    }
}
