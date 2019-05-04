package me.itzsomebody.radon.transformers.obfuscators;

import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Map;

/**
 * Adds {@code @} annotation to all methods
 * Fernflower refuses to decompile the class.
 * Java will crash on attempt to parse annotations
 *
 * @author xDark
 */
public class BadAnnotation extends Transformer {
    @Override
    public void transform() {
        getClassWrappers().parallelStream().forEach(cn -> cn.methods.parallelStream().forEach(mn -> {
            MethodNode methodNode = mn.methodNode;
            if (methodNode.visibleAnnotations == null) {
                methodNode.visibleAnnotations = new ArrayList<>();
            }
            methodNode.visibleAnnotations.add(new AnnotationNode("@"));
            if (methodNode.invisibleAnnotations == null) {
                methodNode.invisibleAnnotations = new ArrayList<>();
            }
            methodNode.invisibleAnnotations.add(new AnnotationNode("@"));
        }));
    }

    @Override
    public String getName() {
        return "BadAnnotation";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.CRASHER;
    }

    @Override
    public Object getConfiguration() {
        return true;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        throw new InvalidConfigurationValueException(ConfigurationSetting.BAD_ANNOTATION + " expects a boolean");
    }
}
