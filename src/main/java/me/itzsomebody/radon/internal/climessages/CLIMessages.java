package me.itzsomebody.radon.internal.climessages;

import me.itzsomebody.radon.Radon;
import me.itzsomebody.radon.utils.LoggerUtils;

/**
 * Class of static abuse LOL
 *
 * @author ItzSomebody
 */
public class CLIMessages {
    /**
     * Prints usage message into console.
     */
    public static void usageMsg() {
        LoggerUtils.stdOut("Usage: java -jar Radon.jar --config example" +
                ".config");
        LoggerUtils.stdOut("USage: java -jar Radon.jar --help");
        LoggerUtils.stdOut("Usage: java -jar Radon.jar");
    }

    /**
     * Prints help message into console.
     */
    public static void helpMsg() {
        LoggerUtils.stdOut("CLI Usage:\t\tjava -jar Radon.jar --config " +
                "example.config");
        LoggerUtils.stdOut("Credits:\t\tjava -jar Radon.jar --version");
        LoggerUtils.stdOut("Help Menu:\t\tjava -jar Radon.jar --help");
        LoggerUtils.stdOut("Watermark Extraction:\tjava -jar Radon.jar " +
                "--extract Input.jar exampleKey");
        LoggerUtils.stdOut("GUI Usage:\t\tjava -jar Radon.jar");
    }
}
