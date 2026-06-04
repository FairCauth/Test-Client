package com.test.mod.transformer.process.impl;

import com.test.mod.asm.tree.MethodNode;
import com.test.mod.transformer.TransformerException;
import com.test.mod.transformer.annotation.ASM;
import com.test.mod.transformer.process.ProcessInfo;
import com.test.mod.transformer.process.TransformerProcess;
import com.test.mod.transformer.utils.Tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ASMProcess extends TransformerProcess<ASM, Method> {
    public ASMProcess() {
        super(ASM.class, Method.class);
    }

    @Override
    public void process(ProcessInfo processInfo, Method method, ASM asm) {
        String desc = asm.desc();
        MethodNode targetMethodNode = getTargetMethodNode(processInfo, asm.methodName(), desc, true);
        MethodNode mixinMethodNode = Tools.getMethod(processInfo.mixinClassNode(), Tools.toDesc(method), method.getName());

        if (targetMethodNode == null || mixinMethodNode == null)
            throw new TransformerException("targetMethodNode or mixinMethodNode NULL!");
        try {
            method.invoke(null, targetMethodNode);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
