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

/**
 * Represents a command switch.
 *
 * @author ItzSomebody.
 */
public class CommandSwitchStatement {
    /**
     * The name of the switch.
     */
    private final String name;

    /**
     * Number of args this switch takes.
     */
    private final int nArgs;

    /**
     * Creates a new {@link CommandSwitchStatement}.
     *
     * @param name  the name of this switch.
     * @param nArgs number of args this switch takes.
     */
    public CommandSwitchStatement(String name, int nArgs) {
        this.name = name;
        this.nArgs = nArgs;
    }

    /**
     * Returns the name of this switch.
     *
     * @return the name of this switch.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of args this switch takes.
     *
     * @return the number of args this switch takes.
     */
    public int getnArgs() {
        return nArgs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CommandSwitchStatement)
            return (((CommandSwitchStatement) obj).getName().equals(this.getName()))
                    && ((CommandSwitchStatement) obj).getnArgs() == this.getnArgs();

        return false;
    }
}
