package me.itzsomebody.radon.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Watermarking utils for the obfuscator.
 *
 * @author ItzSomebody
 */
public class WatermarkUtils { // TODO: Add more secure watermark injection
    /**
     * Extracts injected watermarks by console and stores them in a {@link List} as a {@link String}.
     *
     * @param jarFile file to extract watermarks from.
     * @param key     {@link String} to use to decrypt encrypted watemark messages.
     * @return a {@link List} of all extracted watermarks.
     * @throws Throwable should some virtual-disaster should happen.
     */
    public static List<String> extractWatermark(File jarFile, String key) throws Throwable {
        List<String> foundIds = new ArrayList<>();
        ZipFile zipFile = new ZipFile(jarFile);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        try {
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        ClassReader cr = new ClassReader(in);
                        ClassNode classNode = new ClassNode();
                        cr.accept(classNode, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                        char[] buf = new char[cr.getMaxStringLength()];

                        try {
                            for (int i = 0; i < cr.getItemCount(); i++) {
                                int getItem = cr.getItem(i);
                                String UTF = cr.readUTF8(getItem, buf);
                                if (UTF != null && UTF.startsWith("WMID: ")) {
                                    if (UTF.length() > 6) {
                                        String getId = StringUtils.aesDecrypt(UTF.substring(6, UTF.length()), key);
                                        foundIds.add("Watermarked ID in constant pool of " + entry.getName() + " -> " + getId);
                                    }
                                }
                            }
                        } catch (Throwable t) {
                            //t.printStackTrace();
                        }


                        if (classNode.signature != null) {
                            String decrypted = StringUtils.aesDecrypt(classNode.signature, key);
                            if (decrypted != null) {
                                if (decrypted.startsWith("WMID: ")) {
                                    foundIds.add("Watermarked ID in class signature of " + entry.getName() + " -> " + decrypted);
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            zipFile.close();
        }
        return foundIds;
    }
}
