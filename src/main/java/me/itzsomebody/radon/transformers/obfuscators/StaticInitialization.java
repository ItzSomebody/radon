package me.itzsomebody.radon.transformers.obfuscators;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.ASMUtils;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Moves initialization of all static fields into {@code <clinit>} of the class
 *
 * @author ItzSomebody
 * @author superblaubeere27
 */
public class StaticInitialization extends Transformer {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            MethodNode clinit = classWrapper.getOrCreateClinit();

            classWrapper.fields.stream().filter(fieldWrapper -> !excluded(fieldWrapper)
                    && Modifier.isStatic(fieldWrapper.fieldNode.access)
                    && fieldWrapper.fieldNode.value != null).forEach(fieldWrapper -> {
                FieldNode fieldNode = fieldWrapper.fieldNode;
                Object val = fieldNode.value;

                exit:
                {
                    InsnList toAdd = new InsnList();

                    if (val instanceof String)
                        toAdd.insert(new LdcInsnNode(val));
                    else if (val instanceof Integer)
                        toAdd.insert(ASMUtils.getNumberInsn((Integer) val));
                    else if (val instanceof Long)
                        toAdd.insert(ASMUtils.getNumberInsn((Long) val));
                    else if (val instanceof Float)
                        toAdd.insert(ASMUtils.getNumberInsn((Float) val));
                    else if (val instanceof Double)
                        toAdd.insert(ASMUtils.getNumberInsn((Double) val));
                    else
                        break exit;

                    toAdd.add(new FieldInsnNode(PUTSTATIC, classWrapper.classNode.name, fieldNode.name, fieldNode.desc));
                    clinit.instructions.insert(toAdd);
                    fieldNode.value = null;

                    counter.incrementAndGet();
                }
            });
        });

        Logger.stdOut("Moved " + counter.get() + " field values into static block.");
    }

    @Override
    public String getName() {
        return "Static Initialization";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.STATIC_INITIALIZATION;
    }

    @Override
    public Object getConfiguration() {
        return true;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        // Not needed
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        throw new InvalidConfigurationValueException(ConfigurationSetting.STATIC_INITIALIZATION + " expects a boolean");
    }
}
