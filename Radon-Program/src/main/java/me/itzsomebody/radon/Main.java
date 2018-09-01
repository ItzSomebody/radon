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

import me.itzsomebody.radon.utils.LoggerUtils;

/*
 * TODO: Renamer transformer should correct strings used for reflection. (i.e. Class.forName("me.itzsomebody.Thing"))
 */
public class Main {
    /**
     * Static abuse variables xD
     */
    public static final String PREFIX = "[Radon]";
    public static final String VERSION = "1.0.1";
    public static final String CONTRIBUTORS = "ItzSomebody, x0ark, Col-E, Artel and kazigk";
    public static final String PROPAGANDA_GARBAGE = String.format("Radon is a free and open-source java obfuscator with contributions from %s.\nVersion: %s\nWebsite: https://github.com/ItzSomebody/Radon", Main.CONTRIBUTORS, Main.VERSION);

    /**
     * Main method.
     *
     * @param args arguments from command line.
     */
    public static void main(String[] args) {
        coolThingInConsole();
        new CLI(args);
    }

    /**
     * Logo message for console.
     */
    private static void coolThingInConsole() {
        System.out.println("##############################################");
        System.out.println("# +----------------------------------------+ #");
        System.out.println("# |  _____            _____   ____  _   _  | #");
        System.out.println("# | |  __ \\     /\\   |  __ \\ / __ \\| \\ | | | #");
        System.out.println("# | | |__) |   /  \\  | |  | | |  | |  \\| | | #");
        System.out.println("# | |  _  /   / /\\ \\ | |  | | |  | | . ` | | #");
        System.out.println("# | | | \\ \\  / ____ \\| |__| | |__| | |\\  | | #");
        System.out.println("# | |_|  \\_\\/_/    \\_\\_____/ \\____/|_| \\_| | #");
        System.out.println("# |                                        | #");
        System.out.println("# +----------------------------------------+ #");
        System.out.println("##############################################");
        System.out.println();
        System.out.println();
        LoggerUtils.stdOut("Version: " + VERSION);
        LoggerUtils.stdOut("Contributors: " + CONTRIBUTORS);
    }
}
