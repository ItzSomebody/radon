/*
 * Copyright (C) 2018 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.itzsomebody.radon;

import me.itzsomebody.radon.gui.MainGUI;
import me.itzsomebody.radon.internal.CLI;
import me.itzsomebody.radon.utils.LoggerUtils;

/**
 * The main class :D
 *
 * @author ItzSomebody
 */
public class Radon {
    /**
     * Static abuse variables xD
     */
    public static String PREFIX = "[Radon]";
    public static String VERSION = "0.8.3";
    public static String AUTHORS = "ItzSomebody";

    /**
     * Main method.
     *
     * @param args arguments from command line.
     */
    public static void main(String[] args) {
        coolThingInConsole();

        switch (args.length) {
            case 0:
                new MainGUI();
                break;
            default:
                new CLI(args);
        }
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
        System.out.println("");
        System.out.println("");
        LoggerUtils.stdOut("Version: " + Radon.VERSION);
        LoggerUtils.stdOut("Authors: " + Radon.AUTHORS);
    }
}
