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

package me.itzsomebody.radon.internal.climessages;

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
        LoggerUtils.stdOut("MainGUI Usage:\t\tjava -jar Radon.jar");
    }
}
