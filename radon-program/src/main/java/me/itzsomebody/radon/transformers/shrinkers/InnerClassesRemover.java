package me.itzsomebody.radon.transformers.shrinkers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;

public class InnerClassesRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)
                && classWrapper.classNode.innerClasses != null).forEach(classWrapper -> {
            counter.addAndGet(classWrapper.classNode.innerClasses.size());
            classWrapper.classNode.innerClasses = null;
        });

        Logger.stdOut(String.format("Removed %d inner classes.", counter.get()));
    }

    @Override
    public String getName() {
        return "Inner Classes Remover";
    }
}
