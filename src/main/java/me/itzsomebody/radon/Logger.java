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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utils to print fancy stuff in the console and to write log file.
 * TODO: Switch to {@link java.util.logging.Logger} at some point.
 *
 * @author ItzSomebody
 */
public class Logger {
    /**
     * The {@link SimpleDateFormat} that will be used for logging.
     */
    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
    private final static List<String> STRINGS = new ArrayList<>();

    /**
     * Writes strings to log.
     */
    public static void dumpLog() {
        if (!STRINGS.isEmpty()) {
            BufferedWriter bw;
            try {
                File log = new File("Radon.log");
                if (!log.exists())
                    log.createNewFile();

                bw = new BufferedWriter(new FileWriter(log));
                bw.append(Main.ATTRIBUTION).append('\n');
                STRINGS.forEach(s -> {
                    try {
                        bw.append(s);
                        bw.newLine();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                });
                STRINGS.clear();
                bw.close();
            } catch (Throwable t) {
                stdErr("Error occurred while writing log.");
                t.printStackTrace();
            }
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
        STRINGS.add(formatted);
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
        STRINGS.add(formatted);
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
        STRINGS.add(formatted);
    }
}
