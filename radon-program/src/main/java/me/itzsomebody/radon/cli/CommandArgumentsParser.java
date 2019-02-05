/*
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.itzsomebody.radon.exceptions.CLIException;

public class CommandArgumentsParser {
    private static final List<CommandSwitchStatement> commandSwitches = new LinkedList<>();

    public static void registerCommandSwitch(String name, int nArgs) {
        commandSwitches.add(new CommandSwitchStatement(name, nArgs));
    }

    private final Map<String, String[]> argMap;

    public CommandArgumentsParser(String[] args) {
        argMap = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("--"))
                arg = arg.substring("--".length());
            else if (arg.startsWith("-"))
                arg = arg.substring("-".length());
            else if (arg.startsWith("/"))
                arg = arg.substring("/".length());
            else
                throw new CLIException("Unexpected command argument: " + arg);

            boolean knownSwitch = false;
            for (CommandSwitchStatement cmdSwitch : commandSwitches) {
                if (cmdSwitch.getName().equals(arg.toLowerCase())) {
                    String[] argsArr = new String[cmdSwitch.getnArgs()];

                    for (int j = 0; j < cmdSwitch.getnArgs(); j++) {
                        try {
                            argsArr[j] = args[++i];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            throw new CLIException("Command switch \"" + arg + "\" expected "
                                    + cmdSwitch.getnArgs() + ' ' + ((cmdSwitch.getnArgs() == 1) ? "argument" : "arguments")
                                    + ", got " + j + " instead.");
                        }
                    }

                    argMap.put(arg, argsArr);
                    knownSwitch = true;
                    break;
                }
            }

            if (!knownSwitch)
                throw new CLIException("Unknown command switch: \"" + arg + "\"");
        }
    }

    public boolean containsSwitch(String name) {
        return argMap.containsKey(name);
    }

    public String[] getSwitchArgs(String name) {
        return argMap.get(name);
    }
}
