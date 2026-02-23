package com.test.mod.transformer.process.impl;

import com.test.mod.asm.tree.*;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.TransformerException;
import com.test.mod.transformer.annotation.Shadow;
import com.test.mod.transformer.mapping.Mapping;
import com.test.mod.transformer.process.TransformerProcess;
import com.test.mod.transformer.utils.Tools;

import java.lang.reflect.Method;

public class ShadowMethodProcess extends TransformerProcess<Shadow, Method> {
    public ShadowMethodProcess() {
        super(Shadow.class, Method.class);
    }

    @Override
    public void process(ClassNode targetClassNode, ClassNode mixinClassNode,Class<?> targetClass, ITransformer iTransformer, Method method, Shadow annotation) {
        String desc = annotation.desc();
        if (desc.isEmpty())
            throw new TransformerException("Shadow method desc NULL!!!");
        MethodNode targetMethodNode = null;
        MethodNode mixinMethodNode = Tools.getMethod(mixinClassNode, Tools.toDesc(method), method.getName());
        for (String name : annotation.value()) {
            if(annotation.remap())
                name = Mapping.get(targetClass, name, desc);
            targetMethodNode = Tools.getMethod(targetClassNode, desc, name);
            if (targetMethodNode != null) break;
        }
        if (targetMethodNode == null || mixinMethodNode == null)
            throw new TransformerException("targetMethodNode or mixinMethodNode NULL!");
        rewriteOwner(targetClass, targetClassNode, mixinClassNode, method, annotation);
    }
    private void rewriteOwner(
            Class<?> targetClass,
            ClassNode target,
            ClassNode mixin,
            Method shadowMethod,
            Shadow annotation
    ) {
        String shadowName = shadowMethod.getName();
        String shadowDesc = Tools.toDesc(shadowMethod);
        boolean founded = false;
        for (String s : annotation.value()) {
            for (MethodNode method : mixin.methods) {
                for (AbstractInsnNode insn : method.instructions.toArray()) {
                    if (insn instanceof MethodInsnNode methodInsnNode) {
                        if (methodInsnNode.owner.equals(mixin.name) &&
                                methodInsnNode.name.equals(shadowName) &&
                                methodInsnNode.desc.equals(shadowDesc)) {
                            founded = true;
                            System.out.println("Rewrite Shadow method: " + methodInsnNode.owner + " -> " + target.name);
                            methodInsnNode.owner = target.name;
                            if (annotation.remap())
                                s = Mapping.get(targetClass, s, shadowDesc);
                            methodInsnNode.name = s;

                        }
                    }


                }
            }
            if(founded) break;
        }
    }


}
