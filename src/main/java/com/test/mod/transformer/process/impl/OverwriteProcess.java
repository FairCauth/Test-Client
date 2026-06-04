package com.test.mod.transformer.process.impl;

import com.test.mod.asm.tree.*;
import com.test.mod.transformer.annotation.Overwrite;
import com.test.mod.transformer.process.ProcessInfo;
import com.test.mod.transformer.process.TransformerProcess;
import com.test.mod.transformer.utils.Tools;

import java.lang.reflect.Method;

public class OverwriteProcess extends TransformerProcess<Overwrite, Method> {
    public OverwriteProcess() {
        super(Overwrite.class, Method.class);
    }

    @Override
    public void process(ProcessInfo processInfo,
                        Method method,
                        Overwrite overwrite
    ) {
        String desc = overwrite.desc();
        MethodNode mixinMethodNode = Tools.getMethod(processInfo.mixinClassNode(), Tools.toDesc(method), method.getName());
        MethodNode targetMethodNode = getTargetMethodNode(processInfo, overwrite.methodName(), desc, true);

        if (targetMethodNode != null && mixinMethodNode != null) {
            //System.out.println("11111111111111111111111111111111 " + method.getName());
            MethodNode newMethod = Tools.cloneMethod(mixinMethodNode);
            newMethod.name = targetMethodNode.name;
            newMethod.desc = targetMethodNode.desc;
            newMethod.access = targetMethodNode.access;

            processInfo.targetClassNode().methods.remove(targetMethodNode);
            processInfo.targetClassNode().methods.add(newMethod);
//
//            newMethod.name = targetMethod.name;
//            newMethod.desc = targetMethod.desc;
//            newMethod.access = targetMethod.access;
//            classNode.methods.remove(targetMethod);
//            classNode.methods.add(newMethod);
//            ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
//            while (iterator.hasNext()) {
//                iterator.remove();
//            }
//            InsnList list = new InsnList();
//            methodNode.instructions.insert(methodNode.instructions.getFirst(), list);

        }
    }

}
