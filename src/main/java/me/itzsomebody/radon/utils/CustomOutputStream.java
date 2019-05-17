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
