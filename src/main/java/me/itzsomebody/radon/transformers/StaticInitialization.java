package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.FieldWrapper;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.utils.ASMUtils;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Moves initialization of all static fields into {@code <clinit>} of the class
 *
 * @author xDark
 */
public class StaticInitialization extends Transformer {
    @Override
    public void transform() {
        getClassWrappers().parallelStream().forEach(classWrapper -> {
            Map<FieldWrapper, Object> map = new HashMap<>();
            for (FieldWrapper field : classWrapper.fields) {
                FieldNode node = field.fieldNode;
                if (node.value != null) {
                    if ((node.access & ACC_STATIC) != 0 && (node.value instanceof String
                            || node.value instanceof Integer)) {
                        map.put(field, node.value);
                        node.value = null;
                    }
                }
            }
            if (!map.isEmpty()) {
                InsnList toAdd = new InsnList();
                for (Map.Entry<FieldWrapper, Object> fieldNodeObjectEntry : map.entrySet()) {
                    if (fieldNodeObjectEntry.getValue() instanceof String) {
                        toAdd.add(new LdcInsnNode(fieldNodeObjectEntry.getValue()));
                    }
                    if (fieldNodeObjectEntry.getValue() instanceof Integer) {
                        toAdd.add(ASMUtils.getNumberInsn((Integer) fieldNodeObjectEntry.getValue()));
                    }
                    toAdd.add(
                            new FieldInsnNode(PUTSTATIC, classWrapper.originalName, fieldNodeObjectEntry.getKey().originalName,
                                    fieldNodeObjectEntry.getKey().originalDescription));
                }
                MethodNode clInit = ASMUtils.getMethod(classWrapper.classNode, "<clinit>", "()V");
                if (clInit == null) {
                    clInit = new MethodNode(ACC_STATIC, "<clinit>", "()V", null, new String[0]);
                    classWrapper.addMethod(clInit);
                }

                if (clInit.instructions == null || clInit.instructions.getFirst() == null) {
                    clInit.instructions = toAdd;
                    clInit.instructions.add(new InsnNode(RETURN));
                } else {
                    clInit.instructions.insertBefore(clInit.instructions.getFirst(), toAdd);
                }
            }
        });
    }

    @Override
    public String getName() {
        return "StaticInitialization";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.REFERENCE_OBFUSCATION;
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
        throw new InvalidConfigurationValueException(ConfigurationSetting.STATIC_INITIALIZATION + " expects a boolean");
    }
}
