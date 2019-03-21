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

package me.itzsomebody.radon.cli;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.itzsomebody.radon.exceptions.RadonException;

/**
 * A lame (but 200x better than the old one) commandline argument parser.
 *
 * @author ItzSomebody
 */
public class CommandArgumentsParser {
    /**
     * Currently registered command switches this parser will accept.
     */
    private static final Set<CommandSwitchStatement> SWITCHES = new HashSet<>();

    /**
     * Contains the arguments provided to each switch.
     */
    private final Map<String, String[]> argMap;

    /**
     * Creates a new {@link CommandArgumentsParser} object which parses the provided commandline arguments.
     *
     * @param args provided commandline arguments.
     */
    public CommandArgumentsParser(String[] args) {
        argMap = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            // Is it a switch?
            if (arg.startsWith("--"))
                arg = arg.substring("--".length());
            else if (arg.startsWith("-"))
                arg = arg.substring("-".length());
            else if (arg.startsWith("/"))
                arg = arg.substring("/".length());
            else
                throw new RadonException("Unexpected command argument: " + arg);

            boolean knownSwitch = false;
            for (CommandSwitchStatement cmdSwitch : SWITCHES)
                if (cmdSwitch.getName().equals(arg.toLowerCase())) {
                    String[] argsArr = new String[cmdSwitch.getnArgs()];

                    for (int j = 0; j < cmdSwitch.getnArgs(); j++)
                        try {
                            argsArr[j] = args[++i];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            throw new RadonException(String.format("Command switch \"%s\" expected %d %s, got %d instead.",
                                    arg, cmdSwitch.getnArgs(), (cmdSwitch.getnArgs() == 1) ? "argument" : "arguments",
                                    j));
                        }

                    argMap.put(arg, argsArr);
                    knownSwitch = true;
                    break;
                }

            if (!knownSwitch)
                throw new RadonException("Unknown command switch: \"" + arg + "\"");
        }
    }

    /**
     * Checks if the provided switch name is present in the commandline arguments.
     *
     * @param name switch name to check for.
     * @return true if the provided switch name is present in the commandline arguments.
     */
    public boolean containsSwitch(String name) {
        return argMap.containsKey(name);
    }

    /**
     * Returns the arguments to a switch as a String array.
     *
     * @param name switch name to perform the lookup.
     * @return the arguments to a switch as a String array.
     */
    public String[] getSwitchArgs(String name) {
        return argMap.get(name);
    }

    /**
     * Registers a new switch the {@link CommandArgumentsParser} will accept.
     *
     * @param name  switch name.
     * @param nArgs number of args this switch will take.
     */
    public static void registerCommandSwitch(String name, int nArgs) {
        SWITCHES.add(new CommandSwitchStatement(name, nArgs));
    }
}
