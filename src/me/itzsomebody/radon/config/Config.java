package me.itzsomebody.radon.config;

import me.itzsomebody.radon.transformers.*;
import me.itzsomebody.radon.transformers.flow.HeavyFlowObfuscation;
import me.itzsomebody.radon.transformers.flow.LightFlowObfuscation;
import me.itzsomebody.radon.transformers.flow.NormalFlowObfuscation;
import me.itzsomebody.radon.transformers.invokedynamic.HeavyInvokeDynamic;
import me.itzsomebody.radon.transformers.invokedynamic.LightInvokeDynamic;
import me.itzsomebody.radon.transformers.invokedynamic.NormalInvokeDynamic;
import me.itzsomebody.radon.transformers.linenumbers.ObfuscateLineNumbers;
import me.itzsomebody.radon.transformers.linenumbers.RemoveLineNumbers;
import me.itzsomebody.radon.transformers.localvariables.ObfuscateLocalVariables;
import me.itzsomebody.radon.transformers.localvariables.RemoveLocalVariables;
import me.itzsomebody.radon.transformers.misc.*;
import me.itzsomebody.radon.transformers.renamer.Renamer;
import me.itzsomebody.radon.transformers.sourcedebug.ObfuscateSourceDebug;
import me.itzsomebody.radon.transformers.sourcedebug.RemoveSourceDebug;
import me.itzsomebody.radon.transformers.sourcename.ObfuscateSourceName;
import me.itzsomebody.radon.transformers.sourcename.RemoveSourceName;
import me.itzsomebody.radon.transformers.stringencryption.HeavyStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.LightStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.NormalStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.SuperLightStringEncryption;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Big config class that looks horrible and has lots of docs to make the
 * code look a lot longer than it actually is LOL
 *
 * @author ItzSomebody
 */
public class Config {
    /**
     * Valid keys for element map loaded from config.
     */
    private ArrayList<String> VALIDKEYS = new ArrayList<String>() {
        {
            add("Input");
            add("Output");
            add("Libraries");
            add("Exempts");
            add("StringEncryption");
            add("FlowObfuscation");
            add("InvokeDynamic");
            add("LocalVariableObfuscation");
            add("Crasher");
            add("HideCode");
            add("StringPool");
            add("LineNumberObfuscation");
            add("NumberObfuscation");
            add("SourceNameObfuscation");
            add("SourceDebugObfuscation");
            add("TrashClasses");
            add("WatermarkMessage");
            add("WatermarkType");
            add("WatermarkKey");
            add("SpigotPlugin");
            add("Renamer");
            add("ExpiryTime");
            add("ExpiryMessage");
            add("Shuffler");
            add("InnerClassRemover");
        }
    };

    /**
     * The config object as {@link InputStream}.
     */
    private InputStream config;

    /**
     * The element map object as {@link Map}.
     */
    private Map<String, Object> map;

    /**
     * The exempt list as {@link ArrayList}.
     */
    private List<String> exempts;

    /**
     * Exempted classes from {@link Config#exempts} as {@link ArrayList}.
     */
    private List<String> classExempts;

    /**
     * Exempted methods from {@link Config#exempts} as {@link ArrayList}.
     */
    private List<String> methodExempts;

    /**
     * Exempted fields from {@link Config#exempts} as {@link ArrayList}.
     */
    private List<String> fieldExempts;

    /**
     * Constructs a new {@link Config}
     *
     * @param config the {@link InputStream} to read from.
     */
    public Config(InputStream config) {
        this.config = config;
    }

    /**
     * Loads the configuration elements into {@link Config#map} using {@link Config#config}.
     */
    public void loadIntoMap() {
        this.map = new Yaml().load(this.config);
    }

    /**
     * Checks config for unrecognized keys.
     *
     * @throws IllegalArgumentException if argument is unrecognized.
     */
    public void checkConfig() throws IllegalArgumentException {
        for (String key : this.map.keySet()) {
            if (!this.VALIDKEYS.contains(key)) {
                throw new IllegalArgumentException("Invalid key: " + key);
            }
        }
    }

    /**
     * Returns the input element from {@link Config#map} as {@link File}.
     *
     * @return Returns the input element from {@link Config#map} as {@link File}.
     * @throws IllegalArgumentException if input is null or not a {@link String}.
     */
    public File getInput() throws IllegalArgumentException {
        Object path = this.map.get("Input");
        if (path == null) throw new IllegalArgumentException("Input not specified in config!");
        if (!(path instanceof String)) throw new IllegalArgumentException("Input arg must be a string");

        return new File((String) path);
    }

    /**
     * Returns the output element from {@link Config#map} as {@link File}.
     *
     * @return Returns the output element from {@link Config#map} as {@link File}.
     * @throws IllegalArgumentException if output is not specified as a {@link String}.
     */
    public File getOutput() throws IllegalArgumentException {
        Object path = this.map.get("Output");
        if (path == null) {
            return new File(getInput().getName().replace(".jar", "-OBF.jar"));
        } else {
            if (!(path instanceof String)) throw new IllegalArgumentException("Output arg must be a string");
            return new File((String) path);
        }
    }

    /**
     * Returns the library element from {@link Config#map} as a {@link HashMap}.
     *
     * @return Returns the library element from {@link Config#map} as a {@link HashMap}.
     * @throws IllegalArgumentException if the libraries element is not a list or each list element is not a {@link String}.
     */
    public HashMap<String, File> getLibraries() throws IllegalArgumentException {
        HashMap<String, File> libs = new HashMap<>();
        Object o = this.map.get("Libraries");
        if (o != null) {
            if (!(o instanceof List)) throw new IllegalArgumentException("Libraries must be represented as list");
            List list = (List) o;
            for (Object element : list) {
                if (!(element instanceof String)) throw new IllegalArgumentException("Library args must be string(s)");
                String lib = (String) element;
                libs.put(lib, new File(lib));
            }
        }

        return libs;
    }

    /**
     * Loads the exempt element from {@link Config#map} into {@link Config#exempts}.
     *
     * @throws IllegalArgumentException if the exempt element is not a list or each list element is not a {@link String}.
     */
    private void setExempts() throws IllegalArgumentException {
        this.exempts = new ArrayList<>();
        Object o = this.map.get("Exempts");
        if (o != null) {
            if (!(o instanceof List)) throw new IllegalArgumentException("Exempts must be represented as list");
            List list = (List) o;
            for (Object object : list) {
                if (!(object instanceof String)) throw new IllegalArgumentException("Exemps must be string(s)");
                String value = (String) object;

                this.exempts.add(value);
            }
        }
    }

    /**
     * Sorts the elements in {@link Config#exempts} into {@link Config#classExempts}, {@link Config#methodExempts}.
     * and {@link Config#fieldExempts}
     */
    public void sortExempts() {
        this.setExempts();
        this.classExempts = new ArrayList<>();
        this.methodExempts = new ArrayList<>();
        this.fieldExempts = new ArrayList<>();

        if (this.exempts != null) {
            for (String exempt : this.exempts) {
                if (exempt.endsWith("(METHOD)")) {
                    this.methodExempts.add(exempt.replace("(METHOD)", ""));
                } else if (exempt.endsWith("(FIELD)")) {
                    this.fieldExempts.add(exempt.replace("(FIELD)", ""));
                } else {
                    this.classExempts.add(exempt);
                }
            }
        }
    }

    /**
     * Returns the exempt element from {@link Config#map} as {@link List}.
     *
     * @return Returns the exempt element from {@link Config#map} as {@link List}.
     */
    public List<String> getExempts() {
        return this.exempts;
    }

    /**
     * Returns the class exempts from {@link Config#exempts} as {@link ArrayList}.
     *
     * @return Returns the class exempts from {@link Config#exempts} as {@link ArrayList}
     */
    public List<String> getClassExempts() {
        return this.classExempts;
    }

    /**
     * Returns the method exempts from {@link Config#exempts} as {@link ArrayList}.
     *
     * @return Returns the method exempts from {@link Config#exempts} as {@link ArrayList}.
     */
    public List<String> getMethodExempts() {
        return this.methodExempts;
    }

    /**
     * Returns the field exempts from {@link Config#exempts} as {@link ArrayList}.
     *
     * @return Returns the field exempts from {@link Config#exempts} as {@link ArrayList}.
     */
    public List<String> getFieldExempts() {
        return this.fieldExempts;
    }

    /**
     * Returns the string encryption type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     *
     * @return Returns the string encryption type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is unexpected, null or not a {@link String}
     */
    public AbstractTransformer getStringEncryptionType() throws IllegalArgumentException {
        if (this.map.containsKey("StringEncryption")) {
            Object value = this.map.get("StringEncryption");
            if (value != null) {
                if (!(value instanceof String))
                    throw new IllegalArgumentException("String encryption arg must be a string");
                String s = (String) value;
                if (s.equalsIgnoreCase("SuperLight")) {
                    return new SuperLightStringEncryption(getSpigotBool());
                } else if (s.equalsIgnoreCase("Light")) {
                    return new LightStringEncryption(getSpigotBool());
                } else if (s.equalsIgnoreCase("Normal")) {
                    return new NormalStringEncryption(getSpigotBool());
                } else if (s.equalsIgnoreCase("Heavy")) {
                    return new HeavyStringEncryption(getSpigotBool());
                } else {
                    throw new IllegalArgumentException("Invalid string encryption type: " + s);
                }
            } else {
                throw new IllegalArgumentException("String encryption type is null");
            }
        }

        return null;
    }

    /**
     * Returns the invokedynamic type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     *
     * @return Returns the invokedynamic type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is unexpected, null or not a {@link String}
     */
    public AbstractTransformer getInvokeDynamicType() throws IllegalArgumentException {
        if (this.map.containsKey("InvokeDynamic")) {
            Object value = this.map.get("InvokeDynamic");
            if (value != null) {
                if (!(value instanceof String))
                    throw new IllegalArgumentException("InvokeDynamic arg must be a string");
                String s = (String) value;
                if (s.equalsIgnoreCase("Light")) {
                    return new LightInvokeDynamic();
                } else if (s.equalsIgnoreCase("Normal")) {
                    return new NormalInvokeDynamic();
                } else if (s.equalsIgnoreCase("Heavy")) {
                    return new HeavyInvokeDynamic();
                } else {
                    throw new IllegalArgumentException("Invalid invokedynamic type: " + s);
                }
            } else {
                throw new IllegalArgumentException("InvokeDynamic type is null");
            }
        }

        return null;
    }

    /**
     * Returns the flow obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     *
     * @return Returns the flow obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is unexpected, null or not a {@link String}
     */
    public AbstractTransformer getFlowObfuscationType() throws IllegalArgumentException {
        if (this.map.containsKey("FlowObfuscation")) {
            Object value = this.map.get("FlowObfuscation");
            if (value != null) {
                if (!(value instanceof String))
                    throw new IllegalArgumentException("Flow obfuscation arg must be a string");
                String s = (String) value;
                if (s.equalsIgnoreCase("Light")) {
                    return new LightFlowObfuscation();
                } else if (s.equalsIgnoreCase("Normal")) {
                    return new NormalFlowObfuscation();
                } else if (s.equalsIgnoreCase("Heavy")) {
                    return new HeavyFlowObfuscation();
                } else {
                    throw new IllegalArgumentException("Invalid flow obfuscation type: " + s);
                }
            } else {
                throw new IllegalArgumentException("Flow obfuscation type is null");
            }
        }

        return null;
    }

    /**
     * Returns the local variable obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     *
     * @return Returns the local variable obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is unexpected, null or not a {@link String}
     */
    public AbstractTransformer getLocalVariableObfuscationType() throws IllegalArgumentException {
        if (this.map.containsKey("LocalVariableObfuscation")) {
            Object value = this.map.get("LocalVariableObfuscation");
            if (value != null) {
                if (!(value instanceof String))
                    throw new IllegalArgumentException("Local variable obfuscation arg must be a string");
                String s = (String) value;
                if (s.equalsIgnoreCase("Obfuscate")) {
                    return new ObfuscateLocalVariables();
                } else if (s.equalsIgnoreCase("Remove")) {
                    return new RemoveLocalVariables();
                } else {
                    throw new IllegalArgumentException("Invalid local variable obfuscation type: " + s);
                }
            } else {
                throw new IllegalArgumentException("Local variable obfuscation type is null");
            }
        }

        return null;
    }

    /**
     * Returns the line number obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     *
     * @return Returns the line number obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is unexpected, null or not a {@link String}
     */
    public AbstractTransformer getLineNumberObfuscationType() throws IllegalArgumentException {
        if (this.map.containsKey("LineNumberObfuscation")) {
            Object value = this.map.get("LineNumberObfuscation");
            if (value != null) {
                if (!(value instanceof String))
                    throw new IllegalArgumentException("Line number obfuscation arg must be a string");
                String s = (String) value;
                if (s.equalsIgnoreCase("Obfuscate")) {
                    return new ObfuscateLineNumbers();
                } else if (s.equalsIgnoreCase("Remove")) {
                    return new RemoveLineNumbers();
                } else {
                    throw new IllegalArgumentException("Invalid line number obfuscation type: " + s);
                }
            } else {
                throw new IllegalArgumentException("Line number obfuscation type is null");
            }
        }

        return null;
    }

    /**
     * Returns the source name obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     *
     * @return Returns the source name obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is unexpected, null or not a {@link String}
     */
    public AbstractTransformer getSourceNameObfuscationType() throws IllegalArgumentException {
        if (this.map.containsKey("SourceNameObfuscation")) {
            Object value = this.map.get("SourceNameObfuscation");
            if (value != null) {
                if (!(value instanceof String))
                    throw new IllegalArgumentException("Source name obfuscation arg must be a string");
                String s = (String) value;
                if (s.equalsIgnoreCase("Obfuscate")) {
                    return new ObfuscateSourceName();
                } else if (s.equalsIgnoreCase("Remove")) {
                    return new RemoveSourceName();
                } else {
                    throw new IllegalArgumentException("Invalid source name obfuscation type: " + s);
                }
            } else {
                throw new IllegalArgumentException("Source name obfuscation type is null");
            }
        }

        return null;
    }

    /**
     * Returns the source debug obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     *
     * @return Returns the source debug obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is unexpected, null or not a {@link String}
     */
    public AbstractTransformer getSourceDebugObfuscationType() throws IllegalArgumentException {
        if (this.map.containsKey("SourceDebugObfuscation")) {
            Object value = this.map.get("SourceDebugObfuscation");
            if (value != null) {
                if (!(value instanceof String))
                    throw new IllegalArgumentException("Source debug obfuscation arg must be a string");
                String s = (String) value;
                if (s.equalsIgnoreCase("Obfuscate")) {
                    return new ObfuscateSourceDebug();
                } else if (s.equalsIgnoreCase("Remove")) {
                    return new RemoveSourceDebug();
                } else {
                    throw new IllegalArgumentException("Invalid source debug obfuscation type: " + s);
                }
            } else {
                throw new IllegalArgumentException("Source debug obfuscation type is null");
            }
        }

        return null;
    }

    /**
     * Returns the shuffler type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false
     *
     * @return Returns the shuffler type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     * @throws IllegalArgumentException if value from key is null or not a {@link Boolean}
     */
    public AbstractTransformer getShufflerType() throws IllegalArgumentException {
        if (this.map.containsKey("Shuffler")) {
            Object value = this.map.get("Shuffler");
            if (value != null) {
                if (!(value instanceof Boolean)) throw new IllegalArgumentException("Shuffler arg must be true/false");
                boolean s = (Boolean) value;
                if (s) {
                    return new Shuffler();
                }
            } else {
                throw new IllegalArgumentException("Shuffler arg is null");
            }
        }

        return null;
    }

    /**
     * Returns the inner class remover type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false
     *
     * @return Returns the inner class remover type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     * @throws IllegalArgumentException if value from key is null or not a {@link Boolean}
     */
    public AbstractTransformer getInnerClassRemoverType() throws IllegalArgumentException {
        if (this.map.containsKey("InnerClassRemover")) {
            Object value = this.map.get("InnerClassRemover");
            if (value != null) {
                if (!(value instanceof Boolean))
                    throw new IllegalArgumentException("InnerClassRemover arg must be true/false");
                boolean s = (Boolean) value;
                if (s) {
                    return new InnerClassRemover();
                }
            } else {
                throw new IllegalArgumentException("InnerClassRemover arg is null");
            }
        }

        return null;
    }

    /**
     * Returns the crasher type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false
     *
     * @return Returns the crasher type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     * @throws IllegalArgumentException if value from key is null or not a {@link Boolean}
     */
    public AbstractTransformer getCrasherType() throws IllegalArgumentException {
        if (this.map.containsKey("Crasher")) {
            Object value = this.map.get("Crasher");
            if (value != null) {
                if (!(value instanceof Boolean)) throw new IllegalArgumentException("Crasher arg must be true/false");
                boolean s = (Boolean) value;
                if (s) {
                    return new Crasher();
                }
            } else {
                throw new IllegalArgumentException("Crasher arg is null");
            }
        }

        return null;
    }

    /**
     * Returns the hide code type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     *
     * @return Returns the hide code type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     * @throws IllegalArgumentException if value from key is null or not a {@link Boolean}
     */
    public AbstractTransformer getHideCodeType() throws IllegalArgumentException {
        if (this.map.containsKey("HideCode")) {
            Object value = this.map.get("HideCode");
            if (value != null) {
                if (!(value instanceof Boolean)) throw new IllegalArgumentException("HideCode arg must be true/false");
                boolean s = (Boolean) value;
                if (s) {
                    return new HideCode(getSpigotBool());
                }
            } else {
                throw new IllegalArgumentException("HideCode arg is null");
            }
        }

        return null;
    }

    /**
     * Returns the number of trash classes from {@link Config#map} as {@link Integer}. Defaults to null if null.
     *
     * @return Returns the number of trash classes from {@link Config#map} as {@link Integer}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is null or not a {@link Integer}
     */
    public int getTrashClasses() throws IllegalArgumentException {
        if (this.map.containsKey("TrashClasses")) {
            Object value = this.map.get("TrashClasses");
            if (value != null) {
                if (!(value instanceof Integer))
                    throw new IllegalArgumentException("TrashClasses arg must be an Integer");
                return (Integer) value;
            } else {
                throw new IllegalArgumentException("TrashClasses arg is null");
            }
        }

        return -1;
    }


    /**
     * Returns the watermark message from {@link Config#map} as a {@link String}. Defaults to null if null.
     *
     * @return Returns the crasher type from {@link Config#map} as a {@link String}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is null or not a {@link String}
     */
    public String getWatermarkMsg() throws IllegalArgumentException {
        if (this.map.containsKey("WatermarkMessage")) {
            Object value = this.map.get("WatermarkMessage");
            if (value != null) {
                return value.toString();
            } else {
                throw new IllegalArgumentException("Watermark message is null");
            }
        }

        return null;
    }

    /**
     * Returns the watermark type from {@link Config#map} as an {@link Integer}. Defaults to -1 if null.
     *
     * @return Returns the crasher type from {@link Config#map} as an {@link Integer}. Defaults to -1 if null.
     * @throws IllegalArgumentException if value from key is unexpected, null or not a {@link String}
     */
    public int getWatermarkType() throws IllegalArgumentException {
        if (this.map.containsKey("WatermarkType")) {
            Object value = this.map.get("WatermarkType");
            if (value != null) {
                if (!(value instanceof String))
                    throw new IllegalArgumentException("Watermark type arg must be a string");
                String s = (String) value;
                if (s.equalsIgnoreCase("ConstantPool")) {
                    return 0;
                } else if (s.equalsIgnoreCase("Signature")) {
                    return 1;
                } else {
                    throw new IllegalArgumentException("Invalid watermark type arg: " + value);
                }
            } else {
                throw new IllegalArgumentException("Watermark type arg is null");
            }
        }

        return -1;
    }

    /**
     * Returns the watermark key from {@link Config#map} as a {@link String}. Defaults to null if null.
     *
     * @return Returns the crasher type from {@link Config#map} as a {@link String}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is null or not a {@link String}
     */
    public String getWatermarkKey() throws IllegalArgumentException {
        if (this.map.containsKey("WatermarkKey")) {
            Object value = this.map.get("WatermarkKey");
            if (value != null) {
                return value.toString();
            } else {
                throw new IllegalArgumentException("Watermark key is null");
            }
        }

        return null;
    }

    /**
     * Returns the boolean value of a spigot plugin from {@link Config#map} as a {@link Boolean}. Defaults to false if null.
     *
     * @return Returns the boolean value of a spigot plugin from {@link Config#map} as a {@link Boolean}. Defaults to false if null.
     * @throws IllegalArgumentException if value from key is null or not a {@link Boolean}
     */
    public boolean getSpigotBool() throws IllegalArgumentException {
        if (this.map.containsKey("SpigotPlugin")) {
            Object value = this.map.get("SpigotPlugin");
            if (value != null) {
                if (!(value instanceof Boolean))
                    throw new IllegalArgumentException("Spigot plugin arg must be true/false");
                return (Boolean) value;
            } else {
                throw new IllegalArgumentException("Spigot plugin arg is null");
            }
        }

        return false;
    }

    /**
     * Returns the renamer type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     *
     * @return Returns the renamer type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     * @throws IllegalArgumentException if value from key is null or not a {@link Boolean}
     */
    public AbstractTransformer getRenamerType() throws IllegalArgumentException {
        if (this.map.containsKey("Renamer")) {
            Object value = this.map.get("Renamer");
            if (value != null) {
                if (!(value instanceof Boolean)) throw new IllegalArgumentException("Renamer arg must be true/false");
                boolean s = (Boolean) value;
                if (s) {
                    return new Renamer(getSpigotBool());
                }
            } else {
                throw new IllegalArgumentException("Renamer arg is null");
            }
        }

        return null;
    }

    /**
     * Returns the string pool type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     *
     * @return Returns the string pool type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     * @throws IllegalArgumentException if value from key is null or not a {@link Boolean}
     */
    public AbstractTransformer getStringPoolType() throws IllegalArgumentException {
        if (this.map.containsKey("StringPool")) {
            Object value = this.map.get("StringPool");
            if (value != null) {
                if (!(value instanceof Boolean))
                    throw new IllegalArgumentException("String pool arg must be true/false");
                boolean s = (Boolean) value;
                if (s) {
                    return new StringPool();
                }
            } else {
                throw new IllegalArgumentException("String pool arg is null");
            }
        }

        return null;
    }

    /**
     * Returns the number obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     *
     * @return Returns the number obfuscation type from {@link Config#map} as an {@link AbstractTransformer}. Defaults to null if null or false.
     * @throws IllegalArgumentException if value from key is null or not a {@link Boolean}
     */
    public AbstractTransformer getNumberObfuscationType() throws IllegalArgumentException {
        if (this.map.containsKey("NumberObfuscation")) {
            Object value = this.map.get("NumberObfuscation");
            if (value != null) {
                if (!(value instanceof Boolean))
                    throw new IllegalArgumentException("Number obfuscation arg must be true/false");
                boolean s = (Boolean) value;
                if (s) {
                    return new NumberObfuscation();
                }
            } else {
                throw new IllegalArgumentException("Number obfuscation arg is null");
            }
        }

        return null;
    }

    /**
     * Returns the expiry time from {@link Config#map} as {@link Long}. Defaults to -1.
     *
     * @return the expiry time from {@link Config#map} as {@link Long}. Defaults to -1.
     * @throws IllegalArgumentException if value from key is null or not a {@link String}, and/or is not a proper date format.
     */
    public long getExpiryTime() throws IllegalArgumentException {
        if (this.map.containsKey("ExpiryTime")) {
            Object value = this.map.get("ExpiryTime");
            if (value != null) {
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException("Expiry time must be a string in simple date format");
                }

                String time = (String) value;
                try {
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = format.parse(time);
                    return date.getTime();
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Could not parse date as MM/dd/yyyy");
                }
            }
        }

        return -1;
    }

    /**
     * Returns the expiry message from {@link Config#map} as {@link Integer}. Defaults to null if null.
     *
     * @return Returns the crasher type from {@link Config#map} as {@link String}. Defaults to null if null.
     * @throws IllegalArgumentException if value from key is null or not a {@link String}
     */
    public String getExpiryMsg() throws IllegalArgumentException {
        if (this.map.containsKey("ExpiryMessage")) {
            Object value = this.map.get("ExpiryMessage");
            if (value != null) {
                return value.toString();
            } else {
                throw new IllegalArgumentException("Expiry message is null");
            }
        }

        return null;
    }
}
