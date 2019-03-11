package me.itzsomebody.radon.transformers.shrinkers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;

public class InvisibleParameterAnnotationsRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && methodWrapper.methodNode.invisibleParameterAnnotations != null).forEach(methodWrapper -> {

                    counter.addAndGet(methodWrapper.methodNode.invisibleAnnotableParameterCount);
                    methodWrapper.methodNode.invisibleParameterAnnotations = null;
                }));

        Logger.stdOut(String.format("Removed %d invisible parameter annotations.", counter.get()));
    }

    @Override
    public String getName() {
        return "Invisible Parameter Annotations Remover";
    }
}
