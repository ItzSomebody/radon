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
import me.itzsomebody.radon.Main;

/**
 * Utils to print fancy stuff in the console and to write log file.
 * TODO: Kind of static abuse-ish. Maybe rewrite to use virtual instance instead of static?
 *
 * @author ItzSomebody
 */
public class LoggerUtils {
    /**
     * The {@link SimpleDateFormat} that will be used for logging.
     */
    private static SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");

    private static List<String> strings = new ArrayList<>();

    /**
     * Writes strings to log.
     */
    public static void logWriter() {
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
            bw.append("Version: ").append(Main.VERSION).append('\n');
            bw.append("Contributors: ").append(Main.CONTRIBUTORS).append('\n');
            for (String msg : strings) {
                bw.append(msg);
                bw.newLine();
            }
            strings.clear();
            bw.close();
        } catch (Throwable t) {
            stdErr("Error occurred while writing log.");
            t.printStackTrace();
        }
    }

    /**
     * Prints a formatted message into the console and returns the result as
     * a {@link String}.
     *
     * @param string to write to the console.
     */
    public static void stdOut(String string) {
        String date = FORMAT.format(new Date(System.currentTimeMillis()));
        String formatted = "[" + date + "] INFO: " + string;
        System.out.println(formatted);
        strings.add(formatted);
    }

    /**
     * Prints a formatted message into the console and returns the result as
     * a {@link String}.
     *
     * @param string to write to the console.
     */
    public static void stdErr(String string) {
        String date = FORMAT.format(new Date(System.currentTimeMillis()));
        String formatted = "[" + date + "] ERROR: " + string;
        System.out.println(formatted);
        strings.add(formatted);
    }

    /**
     * Prints a formatted message into the console and returns the result as
     * a {@link String}.
     *
     * @param string to write to the console.
     */
    public static void stdWarn(String string) {
        String date = FORMAT.format(new Date(System.currentTimeMillis()));
        String formatted = "[" + date + "] WARNING: " + string;
        System.out.println(formatted);
        strings.add(formatted);
    }
}
