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

package me.itzsomebody.radon.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class CustomOutputStream extends OutputStream {
    private final BufferedWriter bw;
    private final OutputStream err;

    public CustomOutputStream(OutputStream err) throws IOException {
        File log = new File("Radon.log");
        if (!log.exists())
            log.createNewFile();

        this.bw = new BufferedWriter(new FileWriter(log));
        this.err = err;
    }

    @Override
    public void write(int b) throws IOException {
        bw.append((char) b);
        err.write(b);
    }

    public void close() throws IOException {
        bw.close();
    }
}
