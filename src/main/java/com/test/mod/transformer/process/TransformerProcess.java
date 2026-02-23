package com.test.mod.transformer.process;

import com.test.mod.asm.Opcodes;
import com.test.mod.asm.tree.ClassNode;
import com.test.mod.transformer.ITransformer;
import lombok.Getter;

import java.lang.annotation.Annotation;

public abstract class TransformerProcess<T extends Annotation, V> implements Opcodes {
    @Getter
    private final Class<T> annotationClass;
    @Getter
    private final Class<V> targetType;

    public TransformerProcess(Class<T> annotationClass, Class<V> targetType) {
        this.annotationClass = annotationClass;
        this.targetType = targetType;
    }

    public abstract void process(ClassNode targetClassNode, ClassNode mixinClassNode,Class<?> targetClass, ITransformer iTransformer, V object, T annotation);
}
