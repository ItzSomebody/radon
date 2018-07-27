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

package me.itzsomebody.radon.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import me.itzsomebody.radon.Radon;

/**
 * Utils to print fancy stuff in the console and to write log file.
 *
 * @author ItzSomebody
 */
public class LoggerUtils {
    /**
     * The {@link SimpleDateFormat} that will be used for logging.
     */
    private final static SimpleDateFormat FORMAT
            = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");

    /**
     * Writes an inputted {@link ArrayList} of {@link String}s to a log file.
     *
     * @param strings {@link String}s to write to log file.
     */
    public static void logWriter(List<String> strings) {
        BufferedWriter bw;
        try {
            File log = new File("Radon.log");
            if (!log.exists()) {
                log.createNewFile();
            }
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
     * Prints a formatted message into the console and returns the result as
     * a {@link String}.
     *
     * @param string to write to the console.
     * @return formatted {@link String}.
     */
    public static String stdOut(String string) {
        String date = FORMAT.format(new Date(System.currentTimeMillis()));
        String formatted = "[" + date + "] " + Radon.PREFIX + " - " + string;
        System.out.println(formatted);
        return formatted;
    }
}
