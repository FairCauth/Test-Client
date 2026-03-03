package com.test.mod.transformer.process;

import com.test.mod.asm.Opcodes;
import com.test.mod.asm.tree.ClassNode;
import com.test.mod.asm.tree.MethodNode;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.mapping.Mapping;
import com.test.mod.transformer.utils.Tools;
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
    protected MethodNode getTargetMethodNode(ClassNode targetClassNode, Class<?> targetClass, String[] names, String desc) {
        MethodNode targetMethodNode = null;
        for (String name : names) {
            name = Mapping.get(targetClass, name, desc);
            targetMethodNode = Tools.getMethod(targetClassNode, desc, name);
            if (targetMethodNode != null) break;
        }
        return targetMethodNode;
    }
    public abstract void process(ClassNode targetClassNode, ClassNode mixinClassNode,Class<?> targetClass, Class<? extends ITransformer> iTransformer, V object, T annotation);
}
