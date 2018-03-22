package me.itzsomebody.radon.internal;

import me.itzsomebody.radon.config.Config;
import me.itzsomebody.radon.internal.climessages.CLIMessages;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.Radon;
import me.itzsomebody.radon.utils.WatermarkUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

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
                    CLIMessages.helpMsg();
                    break;
                default:
                    CLIMessages.usageMsg();
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
                    CLIMessages.usageMsg();
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
                    CLIMessages.usageMsg();
                    break;
            }
        } else {
            CLIMessages.usageMsg();
        }
    }
}
