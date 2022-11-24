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

package xyz.itzsomebody.radon.utils.logging;

import xyz.itzsomebody.radon.RadonConstants;
import xyz.itzsomebody.radon.RadonMain;

import java.util.logging.Logger;

/**
 * Wrapper around {@link Logger} to push messages into stdout ({@link System#out}). Parent handler inheritance is
 * switched off.
 *
 * @author itzsomebody
 */
public class RadonLogger {
    /**
     * Static instance of {@link Logger#getAnonymousLogger()}.
     */
    private static final Logger LOGGER;

    static {
        if (RadonConstants.VERBOSE) {
            System.out.println("[RadonLogger] Bootstrapping logger");
        }

        LOGGER = Logger.getLogger("RadonLogger");
        if (RadonConstants.VERBOSE) {
            System.out.println("[RadonLogger] Created logger instance");
        }

        LOGGER.setUseParentHandlers(false); // Avoid parent handler inheritance
        if (RadonConstants.VERBOSE) {
            System.out.println("[RadonLogger] Disabled parent handler inheritance");
        }

        LOGGER.addHandler(new RadonConsoleHandler());
        if (RadonConstants.VERBOSE) {
            System.out.println("[RadonLogger] Registered console handler");
        }
    }

    /**
     * Pushes provided message into stdout ({@link System#out}) via {@link Logger#info(String)}.
     *
     * @param msg Message to output into stdout ({@link System#out}).
     */
    public static void info(final String msg) {
        LOGGER.info(msg);
    }

    /**
     * Pushes provided message into stdout ({@link System#out}) via {@link Logger#warning(String)}.
     *
     * @param msg Message to output into stdout ({@link System#out}).
     */
    public static void warn(final String msg) {
        LOGGER.warning(msg);
    }

    /**
     * Pushes provided message into stdout ({@link System#out}) via {@link Logger#severe(String)}.
     *
     * @param msg Message to output into stdout ({@link System#out}).
     */
    public static void severe(final String msg) {
        LOGGER.severe(msg);
    }
}
