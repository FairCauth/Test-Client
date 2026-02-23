package com.test.mod.transformer.process.impl;

import com.test.mod.asm.tree.*;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.TransformerException;
import com.test.mod.transformer.annotation.At;
import com.test.mod.transformer.annotation.Inject;
import com.test.mod.transformer.annotation.Local;
import com.test.mod.transformer.callback.CallbackInfo;
import com.test.mod.transformer.callback.CallbackInfoReturnable;
import com.test.mod.transformer.mapping.Mapping;
import com.test.mod.transformer.process.TransformerProcess;
import com.test.mod.transformer.utils.PointFinder;
import com.test.mod.transformer.utils.Tools;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class InjectProcess extends TransformerProcess<Inject, Method> {
    public InjectProcess() {
        super(Inject.class, Method.class);
    }

    @Override
    public void process(ClassNode targetClassNode, ClassNode mixinClassNode,Class<?> targetClass, ITransformer iTransformer, Method method, Inject inject) {
        String desc = inject.desc();
        MethodNode targetMethodNode = null;
        MethodNode mixinMethodNode = Tools.getMethod(mixinClassNode, Tools.toDesc(method), method.getName());
        for (String name : inject.methodName()) {
            name = Mapping.get(targetClass, name, desc);
            targetMethodNode = Tools.getMethod(targetClassNode, desc, name);
            if (targetMethodNode != null) break;
        }
        if (targetMethodNode != null && mixinMethodNode != null) {
//            String injectedName = mixinMethodNode.name + "$inject$" + System.nanoTime();
//            MethodNode newMethod = Tools.cloneMethod(mixinMethodNode);
//            newMethod.name = injectedName;
//            newMethod.access = mixinMethodNode.access;

            //jvm不允许插入新方法 redefine class error 63
            //只能将inject方法里的指令全部复制插入 并且偏移变量
//            targetClassNode.methods.add(newMethod);//将新方法添加进目标类

            List<AbstractInsnNode> candidates = PointFinder.find(targetMethodNode, inject.at());
            List<AbstractInsnNode> finalPoints = new ArrayList<>();
            int ordinal = inject.at().ordinal();
            if (ordinal == -1) {
                finalPoints.addAll(candidates);
            } else if (ordinal >= 0 && ordinal < candidates.size()) {
                finalPoints.add(candidates.get(ordinal));
            }

            if (finalPoints.isEmpty()) {
                throw new TransformerException("No Inject points found for @At(\"" + inject.at().value() + "\") target: " + inject.at().target());
            }


           // targetMethodNode.instructions.insert(targetMethodNode.instructions.getFirst(), newInsn);
            for (AbstractInsnNode point : finalPoints) {
                InsnList newInsn = new InsnList();//cloneInsnList(mixinMethodNode.instructions);

                boolean isTargetStatic = (targetMethodNode.access & Opcodes.ACC_STATIC) != 0;
                Type[] targetParams = Type.getArgumentTypes(targetMethodNode.desc);
                //i是目标方法参数占了多少个local
                int i = 0;
                for (Type targetParam : targetParams) {
                    i += targetParam.getSize();
                }
                int currentSlot = (isTargetStatic ? 0 : 1);//变量从哪里开始
                currentSlot += i;//这个是变量开始点 包括currentSlot(已经是变量了 不是参数)
                //callbackinfo
                InsnList callbackInsn = new InsnList();
                boolean callback = false;
                boolean isCIR = false;
                Type targetReturnType = Type.getReturnType(targetMethodNode.desc);
                Class<?>[] parameterTypes = method.getParameterTypes();
                int callbackLocal = currentSlot;
                List<Integer> white = new ArrayList<>();
                Map<Integer, Integer> map = new HashMap<>();//local映射关系
                for (Class<?> parameterType : parameterTypes) {
                    if (parameterType == CallbackInfo.class || parameterType == CallbackInfoReturnable.class) {
                        //当前i要偏移
                        callback = true;
                        isCIR = (parameterType == CallbackInfoReturnable.class);
                        String ciInternal = Type.getInternalName(isCIR ? CallbackInfoReturnable.class : CallbackInfo.class);
                        callbackInsn.add(new TypeInsnNode(Opcodes.NEW, ciInternal));
                        callbackInsn.add(new InsnNode(Opcodes.DUP));
                        callbackInsn.add(new LdcInsnNode(targetMethodNode.name));
                        callbackInsn.add(new InsnNode(inject.cancellable() ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
                        callbackInsn.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ciInternal, "<init>", "(Ljava/lang/String;Z)V", false));
                        callbackInsn.add(new VarInsnNode(Opcodes.ASTORE, callbackLocal));
                        //callbackInsn.add(new VarInsnNode(Opcodes.ALOAD, callbackLocal));
                        break;
                    }

                }
                int localIndex = callbackLocal - (callback ? 0 : 1);//有callback则是 后一个参数
                //local 进行偏移 -> 偏移到local.value()
                Parameter[] parameters = method.getParameters();
                for (Parameter parameter : parameters) {
                    if (parameter.isAnnotationPresent(Local.class)) {
                        Local local = parameter.getAnnotation(Local.class);
                        ++localIndex;
                        int j = local.value();
                        //localIndex -> j;
                        map.put(localIndex, j);
                        white.add(localIndex);
                        System.out.println("aaaaaaaaaaaaaaaaaaaaa");
                    }
                }

                newInsn.add(callbackInsn);
                InsnList mixinInsn = cloneInsnList(mixinMethodNode);


                newInsn.add(mixinInsn);
                if (callback) {
                    callbackInsn.clear();
                    LabelNode skip = new LabelNode();
                    callbackInsn.add(new VarInsnNode(Opcodes.ALOAD, callbackLocal));
                    callbackInsn.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(CallbackInfo.class), "isCancelled", "()Z", false));
                    callbackInsn.add(new JumpInsnNode(Opcodes.IFEQ, skip));
                    if (isCIR && targetReturnType.getSort() != Type.VOID) {
                        callbackInsn.add(new VarInsnNode(Opcodes.ALOAD, callbackLocal));
                        callbackInsn.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(CallbackInfoReturnable.class), "getReturnValue", "()Ljava/lang/Object;", false));

                        applyUnboxing(callbackInsn, targetReturnType);
                        callbackInsn.add(new InsnNode(targetReturnType.getOpcode(Opcodes.IRETURN)));
                    } else {
                        callbackInsn.add(new InsnNode(targetReturnType.getOpcode(Opcodes.IRETURN)));
                    }
                    newInsn.add(callbackInsn);
                    newInsn.add(skip);
                }
                shiftLocals(targetMethodNode, mixinMethodNode, newInsn,isTargetStatic, currentSlot, white);//除了参数的变量全部偏移 包括callbackinfo，但是不能包括local
                //----------------
                Set<Integer> localSet = map.keySet();
                for (Integer origin : localSet) {
                    int target = map.get(origin);
                    //origin -> target
                    specifyLocal(newInsn, origin, target);
                }
                //----------------
                if (inject.at().shift() == At.Shift.AFTER) targetMethodNode.instructions.insert(point, newInsn);
                else targetMethodNode.instructions.insertBefore(point, newInsn);
            }
        }
    }
    private void applyUnboxing(InsnList insns, Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN -> {
                insns.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Boolean"));
                insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
            }
            case Type.INT -> {
                insns.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Integer"));
                insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false));
            }
            case Type.FLOAT -> {
                insns.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Float"));
                insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false));
            }
            case Type.LONG -> {
                insns.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Long"));
                insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false));
            }
            case Type.DOUBLE -> {
                insns.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Double"));
                insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false));
            }
            case Type.OBJECT, Type.ARRAY ->
                    insns.add(new TypeInsnNode(Opcodes.CHECKCAST, type.getInternalName()));
        }
    }
    public static InsnList cloneInsnList(MethodNode methodNode) {
        InsnList cloned = new InsnList();

        Map<LabelNode, LabelNode> labelMap = new HashMap<>();
        for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
            if (insn instanceof LabelNode label) {
                labelMap.put(label, new LabelNode());
            }
        }

        for (AbstractInsnNode insn : methodNode.instructions.toArray()) {

            int opcode = insn.getOpcode();

            // 删除所有 return
            if (opcode == RETURN || opcode == IRETURN || opcode == LRETURN
                    || opcode == FRETURN || opcode == DRETURN || opcode == ARETURN) {
                continue;
            }

            cloned.add(insn.clone(labelMap));
        }

        return cloned;
    }
    public static void specifyLocal(InsnList injected, int ori, int target) {
        for (AbstractInsnNode insn : injected.toArray()) {
            if (insn instanceof VarInsnNode varInsn) {
                if (varInsn.var == ori) {
                    varInsn.var = target;
                    System.out.println("specifyLocal " + ori + " -> " + target);
                }
            }
            // 处理 IINC 指令
            if (insn instanceof IincInsnNode iinc) {
                if (iinc.var == ori) {
                    iinc.var = target;
                    System.out.println("specifyLocal " + ori + " -> " + target);
                }
            }
        }
        //System.out.println(targetMethod.maxLocals + " "+mixinMethod.maxLocals + " mixinMethod.maxLocals");

    }
    public static void shiftLocals(MethodNode targetMethod,
                                   MethodNode mixinMethod,
                                   InsnList injected,boolean isStatic, int start, List<Integer> white) {

        int offset = targetMethod.maxLocals;

        for (AbstractInsnNode insn : injected.toArray()) {


            if (insn instanceof VarInsnNode varInsn) {
                if (varInsn.var >= start) {
                    if(!white.contains(varInsn.var)) //不在白名单内
                        varInsn.var += offset;
                }

            }


            // 处理 IINC 指令
            if (insn instanceof IincInsnNode iinc) {
                int v = iinc.var;
                if (v >= start) {
                    if(!white.contains(v))
                        iinc.var += offset;
                }

            }
        }
        System.out.println(targetMethod.maxLocals + " "+mixinMethod.maxLocals + " mixinMethod.maxLocals " + mixinMethod.name);
        // 更新 target 的 maxLocals
        targetMethod.maxLocals += mixinMethod.maxLocals;
    }

}
