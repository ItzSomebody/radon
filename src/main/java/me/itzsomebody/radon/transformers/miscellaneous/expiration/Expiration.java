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

package me.itzsomebody.radon.transformers.miscellaneous.expiration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;


/**
 * Inserts an expiration block of instructions in each constructor method.
 *
 * @author ItzSomebody
 */
public class Expiration extends Transformer {
    private static final Map<String, ExpirationSetting> KEY_MAP = new HashMap<>();
    private String message;
    private long expires;
    private boolean injectJOptionPaneEnabled;

    static {
        Stream.of(ExpirationSetting.values()).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
    }

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.getClassNode();

            classNode.methods.stream().filter(methodNode -> "<init>".equals(methodNode.name)).forEach(methodNode -> {
                InsnList expirationCode = createExpirationInstructions();
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), expirationCode);
                counter.incrementAndGet();
            });
        });

        Main.info(String.format("Added %d expiration code blocks.", counter.get()));
    }

    private InsnList createExpirationInstructions() {
        InsnList insns = new InsnList();
        LabelNode injectedLabel = new LabelNode(new Label());

        insns.add(new TypeInsnNode(NEW, "java/util/Date"));
        insns.add(new InsnNode(DUP));
        insns.add(new MethodInsnNode(INVOKESPECIAL, "java/util/Date", "<init>", "()V", false));
        insns.add(new TypeInsnNode(NEW, "java/util/Date"));
        insns.add(new InsnNode(DUP));
        insns.add(new LdcInsnNode(getExpires()));
        insns.add(new MethodInsnNode(INVOKESPECIAL, "java/util/Date", "<init>", "(J)V", false));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Date", "after", "(Ljava/util/Date;)Z", false));
        insns.add(new JumpInsnNode(IFEQ, injectedLabel));
        insns.add(new TypeInsnNode(NEW, "java/lang/Throwable"));
        insns.add(new InsnNode(DUP));
        insns.add(new LdcInsnNode(getMessage()));
        if (isInjectJOptionPaneEnabled()) {
            insns.add(new InsnNode(DUP));
            insns.add(new InsnNode(ACONST_NULL));
            insns.add(new InsnNode(SWAP));
            insns.add(new MethodInsnNode(INVOKESTATIC, "javax/swing/JOptionPane", "showMessageDialog",
                    "(Ljava/awt/Component;Ljava/lang/Object;)V", false));
        }
        insns.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Throwable", "<init>", "(Ljava/lang/String;)V", false));
        insns.add(new InsnNode(ATHROW));
        insns.add(injectedLabel);

        return insns;
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.EXPIRATION;
    }

    @Override
    public String getName() {
        return "Expiration";
    }

    @Override
    public Object getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        config.put(ExpirationSetting.EXPIRATION_DATE.getName(), getExpires());
        config.put(ExpirationSetting.INJECT_JOPTIONPAN.getName(), isInjectJOptionPaneEnabled());
        config.put(ExpirationSetting.EXPIRATION_MESSAGE.getName(), getMessage());

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        setExpires(getValueOrDefault(ExpirationSetting.EXPIRATION_DATE.getName(), config, "12/31/2020")); // TODO: convert to long
        setInjectJOptionPaneEnabled(getValueOrDefault(ExpirationSetting.INJECT_JOPTIONPAN.getName(), config, false));
        setMessage(getValueOrDefault(ExpirationSetting.EXPIRATION_MESSAGE.getName(), config, "Your trial has expired!"));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            ExpirationSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.EXPIRATION.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.EXPIRATION.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    private String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    private long getExpires() {
        return expires;
    }

    private void setExpires(String expires) {
        try {
            this.expires = new SimpleDateFormat("MM/dd/yyyy").parse(expires).getTime();
        } catch (ParseException e) {
            Main.severe("Error while parsing time.");
            e.printStackTrace();
            throw new RadonException();
        }
    }

    private boolean isInjectJOptionPaneEnabled() {
        return injectJOptionPaneEnabled;
    }

    private void setInjectJOptionPaneEnabled(boolean injectJOptionPaneEnabled) {
        this.injectJOptionPaneEnabled = injectJOptionPaneEnabled;
    }
}
