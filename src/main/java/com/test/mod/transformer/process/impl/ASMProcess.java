package com.test.mod.transformer.process.impl;

import com.test.mod.asm.tree.ClassNode;
import com.test.mod.asm.tree.MethodNode;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.TransformerException;
import com.test.mod.transformer.annotation.ASM;
import com.test.mod.transformer.mapping.Mapping;
import com.test.mod.transformer.process.TransformerProcess;
import com.test.mod.transformer.utils.Tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ASMProcess extends TransformerProcess<ASM, Method> {
    public ASMProcess() {
        super(ASM.class, Method.class);
    }

    @Override
    public void process(ClassNode targetClassNode,
                        ClassNode mixinClassNode,
                        Class<?> targetClass,
                        Class<? extends ITransformer> iTransformer,
                        Method method,
                        ASM asm
    ) {
        String desc = asm.desc();
        MethodNode targetMethodNode = getTargetMethodNode(targetClassNode, targetClass, asm.methodName(), desc);
        MethodNode mixinMethodNode = Tools.getMethod(mixinClassNode, Tools.toDesc(method), method.getName());

        if (targetMethodNode == null || mixinMethodNode == null)
            throw new TransformerException("targetMethodNode or mixinMethodNode NULL!");
        try {
            method.invoke(null, targetMethodNode);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
