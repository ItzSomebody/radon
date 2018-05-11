package me.itzsomebody.radon.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Constructs a config in memory and writes to file.
 *
 * @author ItzSomebody
 */
public class ConfigWriter {
    /**
     * Key -> Value map.
     */
    private Map<ConfigEnum, Object> keyValueMap;

    /**
     * Lines to write to config.
     */
    private List<String> lines = new ArrayList<>();

    /**
     * Creates a new ConfigWriter object.
     *
     * @param keyValueMap Key -> Value map.
     */
    public ConfigWriter(Map<ConfigEnum, Object> keyValueMap) {
        this.keyValueMap = keyValueMap;
    }

    /**
     * Parses all options into a virtual config.
     */
    public void parseOptions() {
        Object result = this.keyValueMap.get(ConfigEnum.INPUT);
        if (result != null) {
            lines.add("Input: \"" + result.toString().replace("\\", "/") + "\"");
        }

        result = this.keyValueMap.get(ConfigEnum.OUTPUT);
        if (result != null) {
            lines.add("Output: \"" + result.toString().replace("\\", "/") + "\"");
        }

        result = this.keyValueMap.get(ConfigEnum.STRING_ENCRYPTION);
        if (result != null) {
            lines.add("StringEncryption: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.INVOKEDYNAMIC);
        if (result != null) {
            lines.add("InvokeDynamic: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.FLOW_OBFUSCATION);
        if (result != null) {
            lines.add("FlowObfuscation: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.LOCAL_VARIABLES);
        if (result != null) {
            lines.add("LocalVariableObfuscation: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.CRASHER);
        if (result != null) {
            lines.add("Crasher: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.HIDER);
        if (result != null) {
            lines.add("HideCode: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.STRING_POOL);
        if (result != null) {
            lines.add("StringPool: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.LINE_NUMBERS);
        if (result != null) {
            lines.add("LineNumberObfuscation: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.NUMBERS);
        if (result != null) {
            lines.add("NumberObfuscation: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.SOURCE_NAME);
        if (result != null) {
            lines.add("SourceNameObfuscation: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.SOURCE_DEBUG);
        if (result != null) {
            lines.add("SourceDebugObfuscation: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.TRASH_CLASSES);
        if (result != null) {
            lines.add("TrashClasses: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.WATERMARK_MSG);
        if (result != null) {
            lines.add("WatermarkMessage: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.WATERMARK_TYPE);
        if (result != null) {
            lines.add("WatermarkType: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.WATERMARK_KEY);
        if (result != null) {
            lines.add("WatermarkKey: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.SPIGOT_PLUGIN);
        if (result != null) {
            lines.add("SpigotPlugin: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.RENAMER);
        if (result != null) {
            lines.add("Renamer: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.EXPIRATION_TIME);
        if (result != null) {
            lines.add("ExpiryTime: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.EXPIRATION_MESSAGE);
        if (result != null) {
            lines.add("ExpiryMessage: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.SHUFFLER);
        if (result != null) {
            lines.add("Shuffler: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.DICTIONARY);
        if (result != null) {
            lines.add("Dictionary: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.INNERCLASSES);
        if (result != null) {
            lines.add("InnerClassRemover: " + result);
        }

        result = this.keyValueMap.get(ConfigEnum.LIBRARIES);
        if (result != null) {
            List<String> libs = (List) result;
            if (!libs.isEmpty()) {
                lines.add("Libraries: ");
                for (String lib : libs) {
                    lines.add("    - \"" + lib.replace("\\", "/") + "\"");
                }
            }
        }

        result = this.keyValueMap.get(ConfigEnum.EXEMPTS);
        if (result != null) {
            List<String> exempts = (List) result;
            if (!exempts.isEmpty()) {
                lines.add("Exempts: ");
                for (String exempt : exempts) {
                    lines.add("    - \"" + exempt + "\"");
                }
            }
        }
    }

    /**
     * Writes config to a file.
     *
     * @throws IOException if the file already exists, is an output or some
     *                     other weird thing happens.
     */
    public void writeConfig(String path) throws IOException {
        File output = new File(path);
        if (output.exists())
            throw new IOException(path + " already exists!");

        if (output.isDirectory())
            throw new IOException(path + " needs to be a file, not a directory");

        output.createNewFile();
        BufferedWriter stream = new BufferedWriter(new FileWriter(output));
        for (String line : this.lines) {
            stream.write(line);
            stream.write('\n');
        }
        stream.close();
    }
}
