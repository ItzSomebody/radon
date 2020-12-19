/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2020 ItzSomebody
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

package xyz.itzsomebody.radon.cli;

import xyz.itzsomebody.radon.exceptions.PreventableRadonException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class CmdArgsParser {
    private final static String[] SWITCH_PREFIXES = {"--", "-", "/"};
    private final static Set<CmdSwitch> SWITCHES = new HashSet<>();
    private final Map<String, String[]> argMap = new HashMap<>();

    private String getSwitchIdOrThrow(String arg) {
        return arg.substring(Stream.of(SWITCH_PREFIXES)
                .filter(arg::startsWith)
                .findFirst()
                .orElseThrow(() -> new PreventableRadonException("Unexpected command argument: \"" + arg + "\""))
                .length());
    }

    public void parse(String[] args) {
        for (var cliArgIndex = 0; cliArgIndex < args.length; cliArgIndex++) {
            final var switchId = getSwitchIdOrThrow(args[cliArgIndex]);

            var knownSwitch = false;
            for (var cmdSwitch : SWITCHES) {
                if (cmdSwitch.id.equals(switchId)) {
                    String[] switchArgs = new String[cmdSwitch.expectedArgs];

                    for (var switchArgIndex = 0; switchArgIndex < cmdSwitch.expectedArgs; switchArgIndex++) {
                        try {
                            switchArgs[switchArgIndex] = args[++cliArgIndex];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            throw new PreventableRadonException(String.format("Command switch \"%s\" expected %d argument(s), got %d instead.",
                                    switchId,
                                    cmdSwitch.expectedArgs,
                                    switchArgIndex
                            ));
                        }
                    }

                    argMap.put(switchId, switchArgs);
                    knownSwitch = true;
                    break;
                }
            }

            if (!knownSwitch) {
                throw new PreventableRadonException("Unknown command switch: \"" + switchId + "\"");
            }
        }
    }

    public static void registerSwitch(String id, int expectedArgs) {
        registerSwitch(new CmdSwitch(id, expectedArgs));
    }

    public static void registerSwitch(CmdSwitch cmdSwitch) {
        SWITCHES.add(cmdSwitch);
    }

    public boolean containsSwitch(String id) {
        return argMap.containsKey(id);
    }

    public String[] getArgsFor(String id) {
        return argMap.get(id);
    }
}
