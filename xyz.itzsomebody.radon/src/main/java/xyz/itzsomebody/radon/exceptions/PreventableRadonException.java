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

package xyz.itzsomebody.radon.exceptions;

/**
 * A subclass of {@link RuntimeException} that should only be thrown on user-preventable exceptions
 * (e.g. certain OOM errors).
 *
 * @author itzsomebody
 */
public class PreventableRadonException extends RuntimeException {
    public PreventableRadonException() {
        super();
    }

    public PreventableRadonException(String msg) {
        super(msg);
    }

    public PreventableRadonException(Throwable t) {
        super(t);
    }
}
