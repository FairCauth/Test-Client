package com.test.mod.transformer.process.impl;

import com.test.mod.asm.Type;
import com.test.mod.asm.tree.*;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.TransformerException;
import com.test.mod.transformer.annotation.At;
import com.test.mod.transformer.annotation.Hook;
import com.test.mod.transformer.mapping.Mapping;
import com.test.mod.transformer.process.TransformerProcess;
import com.test.mod.transformer.utils.PointFinder;
import com.test.mod.transformer.utils.Tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HookProcess extends TransformerProcess<Hook, Method> {
    public HookProcess() {
        super(Hook.class, Method.class);
    }

    @Override
    public void process(ClassNode targetClassNode,
                        ClassNode mixinClassNode,
                        Class<?> targetClass,
                        Class<? extends ITransformer> iTransformer,
                        Method method,
                        Hook annotation
    ) {
        String desc = annotation.desc();

        MethodNode mixinMethodNode = Tools.getMethod(mixinClassNode, Tools.toDesc(method), method.getName());
        MethodNode targetMethodNode = getTargetMethodNode(targetClassNode, targetClass, annotation.methodName(), desc, true);

        if (targetMethodNode == null || mixinMethodNode == null)
            throw new TransformerException("targetMethodNode or mixinMethodNode NULL!");

        List<AbstractInsnNode> candidates = PointFinder.find(targetMethodNode, annotation.at());
        List<AbstractInsnNode> finalPoints = new ArrayList<>();
        int ordinal = annotation.at().ordinal();
        if (ordinal == -1) {
            finalPoints.addAll(candidates);
        } else if (ordinal >= 0 && ordinal < candidates.size()) {
            finalPoints.add(candidates.get(ordinal));
        }

        if (finalPoints.isEmpty()) {
            throw new TransformerException("No Inject points found for @At(\"" + annotation.at().value() + "\") target: " + annotation.at().target());
        }
       for (AbstractInsnNode point : finalPoints) {
           InsnList newInsn = new InsnList();
           //加载参数
//           for (int local : annotation.locals()) {
//               if(local == 0) {
//                   newInsn.add(new VarInsnNode(ALOAD, 0));//this
//               }else {
//                   Type type = findLocalType(targetMethodNode, local);
//                   newInsn.add(new VarInsnNode(getLoadOpcode(type), local));
//               }
//
//           }
           int i = 0;
           for (int local : annotation.locals()) {
               newInsn.add(new VarInsnNode(annotation.types()[i], local));
               i++;
           }
           newInsn.add(new MethodInsnNode(
                   INVOKESTATIC, Type.getInternalName(method.getDeclaringClass()), method.getName(),Tools.toDesc(method)
           ));
           if (annotation.at().shift() == At.Shift.AFTER) targetMethodNode.instructions.insert(point, newInsn);
            else targetMethodNode.instructions.insertBefore(point, newInsn);
        }

    }
    private Type findLocalType(MethodNode methodNode, int slot) {
        if (methodNode.localVariables == null) {
            throw new RuntimeException("No LocalVariableTable present");
        }

        for (LocalVariableNode var : methodNode.localVariables) {
            if (var.index == slot) {
                return Type.getType(var.desc);
            }
        }

        throw new RuntimeException("Local variable not found for slot: " + slot);
    }
    private int getLoadOpcode(Type type) {
        return switch (type.getSort()) {
            case Type.INT, Type.BOOLEAN, Type.BYTE, Type.CHAR, Type.SHORT -> ILOAD;
            case Type.FLOAT -> FLOAD;
            case Type.LONG -> LLOAD;
            case Type.DOUBLE -> DLOAD;
            case Type.OBJECT, Type.ARRAY -> ALOAD;
            default -> throw new RuntimeException("Unsupported type: " + type);
        };
    }
}
