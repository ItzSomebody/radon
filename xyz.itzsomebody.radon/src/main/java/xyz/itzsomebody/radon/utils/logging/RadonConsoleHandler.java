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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A {@link StreamHandler} which outputs to stdout ({@link System#out}).
 *
 * @author itzsomebody
 */
public class RadonConsoleHandler extends StreamHandler {
    /**
     * Constructs a {@link StreamHandler} which outputs to stdout ({@link System#out}).
     * <p>
     * The {@link String} pushed into stdout is composed of a timestamp, level, and message.
     */
    public RadonConsoleHandler() {
        super(System.out, new Formatter() {
            @Override
            public String format(final LogRecord record) {
                var formatStr = "[%s] %s: %s\n";
                var dateStr = new SimpleDateFormat(RadonConstants.LOG_TIMESTAMP_FORMAT).format(new Date(record.getMillis()));
                var levelName = record.getLevel().getName();
                var formattedMsg = formatMessage(record);

                return String.format(formatStr, dateStr, levelName, formattedMsg);
            }
        });
    }
}