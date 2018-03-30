package me.itzsomebody.radon;

import me.itzsomebody.radon.gui.GUI;
import me.itzsomebody.radon.internal.Bootstrap;
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
    public static String VERSION = "0.5.3";
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
                new GUI();
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
