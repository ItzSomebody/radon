package xyz.itzsomebody.radon.transformers.strings;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.expressions.IRExpressions;
import xyz.itzsomebody.commons.InsnListModifier;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.asm.ClassWrapper;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

/**
 * Takes all the strings in a class and stuffs them into a method which initializes a static field when
 * <clinit> is invoked.
 *
 * @author itzsomebody
 */
public class StaticFieldStrPool extends StringTransformer {
    @Override
    public void transform() {
        AtomicInteger count = new AtomicInteger();

        classStream().filter(this::notExcluded).forEach(classWrapper -> {
            var strList = new ArrayList<String>();
            String tempMethodName;
            do {
                tempMethodName = dictionary.next();
            } while (classWrapper.containsMethodNode(tempMethodName, "()V"));
            String tempFieldName;
            do {
                tempFieldName = dictionary.next();
            } while (classWrapper.containsFieldNode(tempFieldName, "[Ljava/lang/String;"));

            final String stringPoolInitMethodName = tempMethodName;
            final String stringPoolFieldName = tempFieldName;
            classWrapper.methodStream().filter(mw -> notExcluded(mw) && mw.hasInstructions()).forEach(methodWrapper -> {
                var methodNode = methodWrapper.getMethodNode();
                var modifier = new InsnListModifier();
                methodNode.instructions.forEach(insn -> {
                    if (insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst instanceof String) {
                        var str = (String) ((LdcInsnNode) insn).cst;

                        if (!isExcludedString(str)) {
                            modifier.replace(insn, getArrayElement(
                                    getStatic(WrappedType.fromInternalName(classWrapper.getName(), classWrapper.isInterface()), stringPoolFieldName, WrappedType.from(String[].class)),
                                    intConst(strList.size())
                            ).getInstructions().compile());
                            strList.add(str);
                            count.incrementAndGet();
                        }
                    }
                });
                modifier.apply(methodNode.instructions);
            });

            if (strList.size() != 0) {
                classWrapper.addMethod(createStringPoolInitializer(classWrapper, stringPoolInitMethodName, stringPoolFieldName, strList));

                var clinit = classWrapper.getMethodNode("<clinit>", "()V");
                if (clinit == null) {
                    clinit = new MethodNode(ACC_PRIVATE | ACC_STATIC, "<clinit>", "()V", null, null);
                    clinit.visitMethodInsn(INVOKESTATIC, classWrapper.getName(), stringPoolInitMethodName, "()V", false);
                    clinit.visitInsn(RETURN);
                    classWrapper.addMethod(clinit);
                } else {
                    clinit.instructions.insert(new MethodInsnNode(INVOKESTATIC, classWrapper.getName(), stringPoolInitMethodName, "()V", false));
                }

                classWrapper.addField(new FieldNode(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, stringPoolFieldName, "[Ljava/lang/String;", null, null));
            }
        });

        RadonLogger.info("Pooled " + count.get() + " strings");
    }

    @Override
    public String getConfigName() {
        return Transformers.POOL_STRINGS_TO_STATIC_FIELD.getConfigName();
    }

    private static MethodNode createStringPoolInitializer(ClassWrapper owner, String name, String stringPool, ArrayList<String> strings) {
        var method = new MethodNode(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC | ACC_BRIDGE, name, "()V", null, null);
        var createArrayExpr = newArray(String.class, strings.stream().map(IRExpressions::stringConst).toArray(IRExpression[]::new));
        var putStaticExpr = setStatic(WrappedType.fromInternalName(owner.getName(), owner.isInterface()), stringPool, WrappedType.from(String[].class), createArrayExpr);
        method.instructions = putStaticExpr.getInstructions().voidReturn().compile();
        return method;
    }
}
