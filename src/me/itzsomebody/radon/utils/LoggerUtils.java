package me.itzsomebody.radon.utils;

import me.itzsomebody.radon.Radon;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utils to print fancy stuff in the console and to write log file.
 *
 * @author ItzSomebody
 */
public class LoggerUtils {
    /**
     * The {@link SimpleDateFormat} that will be used for logging.
     */
    private static SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");

    /**
     * Writes an inputted {@link ArrayList} of {@link String}s to a log file.
     *
     * @param strings {@link String}s to write to log file.
     */
    public static void logWriter(List<String> strings) {
        String date = FORMAT.format(new Date(System.currentTimeMillis()));
        BufferedWriter bw;
        try {
            File log = new File("Radon.log");
            if (!log.exists()) {
                log.createNewFile();
            }
            //bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(log), StandardCharsets.UTF_8));
            bw = new BufferedWriter(new FileWriter(log));
            bw.append("##############################################\n");
            bw.append("# +----------------------------------------+ #\n");
            bw.append("# |  _____            _____   ____  _   _  | #\n");
            bw.append("# | |  __ \\     /\\   |  __ \\ / __ \\| \\ | | | #\n");
            bw.append("# | | |__) |   /  \\  | |  | | |  | |  \\| | | #\n");
            bw.append("# | |  _  /   / /\\ \\ | |  | | |  | | . ` | | #\n");
            bw.append("# | | | \\ \\  / ____ \\| |__| | |__| | |\\  | | #\n");
            bw.append("# | |_|  \\_\\/_/    \\_\\_____/ \\____/|_| \\_| | #\n");
            bw.append("# |                                        | #\n");
            bw.append("# +----------------------------------------+ #\n");
            bw.append("##############################################\n");
            bw.append("\n");
            bw.append("\n");
            bw.append("Version: " + Radon.VERSION + "\n");
            bw.append("Author: " + Radon.AUTHORS + "\n");
            for (String msg : strings) {
                bw.append(msg);
                bw.newLine();
            }
            bw.close();
        } catch (Throwable t) {
            stdOut("Error: " + t.getMessage());
        }
    }

    /**
     * Prints a formatted message into the console and returns the result as a {@link String}.
     *
     * @param string to write to the console.
     * @return formatted {@link String}.
     */
    public static String stdOut(String string) {
        String date = FORMAT.format(new Date(System.currentTimeMillis()));
        String formatted = "[" + date + "] " + Radon.PREFIX + " - " + string;
        /*try {
            new PrintStream(System.out, true, "UTF-8").println(formatted);
        } catch (Throwable t) {
            ;
        }*/
        System.out.println(formatted);
        return formatted;
    }
}
