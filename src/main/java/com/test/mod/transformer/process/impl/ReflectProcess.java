package com.test.mod.transformer.process.impl;

import com.test.mod.asm.Type;
import com.test.mod.asm.tree.*;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.TransformerException;
import com.test.mod.transformer.annotation.Reflect;
import com.test.mod.transformer.mapping.Mapping;
import com.test.mod.transformer.process.TransformerProcess;
import com.test.mod.transformer.utils.Tools;
import com.test.mod.transformer.varhandle.VarHandleCache;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

//redefine error 64无法修改字节码
public class ReflectProcess extends TransformerProcess<Reflect, Method> {
    public ReflectProcess() {
        super(Reflect.class, Method.class);
    }

    @Override
    public void process(ClassNode targetClassNode,
                        ClassNode mixinClassNode,
                        Class<?> targetClass,
                        Class<? extends ITransformer> iTransformer,
                        Method object,
                        Reflect annotation) {
        String fieldName = annotation.value();
        fieldName = Mapping.get(targetClass,fieldName, null);
        boolean isMethod = !annotation.desc().isEmpty();
        if(fieldName == null) return;
        boolean isStatic = false;
        String key = targetClass.getName() + "." + fieldName + "." + annotation.desc();
        try {
            if (isMethod) {
                Type r = Type.getMethodType(annotation.desc());
                Type[] argTypes = r.getArgumentTypes();

                Class<?>[] paramTypes = new Class<?>[argTypes.length];
                for (int i = 0; i < argTypes.length; i++) {
                    paramTypes[i] = Class.forName(argTypes[i].getClassName());
                }

                Method method = targetClass.getDeclaredMethod(fieldName, paramTypes);
                isStatic = Modifier.isStatic(method.getModifiers());

                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodHandles.Lookup privateLookup =
                        MethodHandles.privateLookupIn(targetClass, lookup);

                MethodType methodType =
                        MethodType.methodType(method.getReturnType(), method.getParameterTypes());

                MethodHandle methodHandle;

                if (isStatic) {
                    methodHandle = privateLookup.findStatic(
                            targetClass,
                            fieldName,
                            methodType
                    );
                } else {
                    methodHandle = privateLookup.findVirtual(
                            targetClass,
                            fieldName,
                            methodType
                    );
                }

                if (methodHandle == null) {
                    System.out.println("NULL methodHandle!!!");
                    return;
                }

                System.out.println("PUT METHOD CACHE " + key);
                VarHandleCache.putMethodCache(key, methodHandle);

            }else {
                Field field = targetClass.getDeclaredField(fieldName);
                Class<?> fieldType = field.getType();
                isStatic = Modifier.isStatic(field.getModifiers());
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodHandles.Lookup privateLookup =
                        MethodHandles.privateLookupIn(targetClass, lookup);
                VarHandle varHandle = privateLookup.findVarHandle(
                        targetClass, fieldName, fieldType);
                if(varHandle == null) {
                    System.out.println("NULL varHandle!!!");
                    return;
                }
                System.out.println("PUT FIELD CACHE " + key );
                VarHandleCache.putFieldCache(key, varHandle);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }


        String[] strings = { object.getName() };
        MethodNode mixinMethodNode = getTargetMethodNode(mixinClassNode, iTransformer, strings, Tools.toDesc(object), false);
        if(mixinMethodNode == null) {
            throw new TransformerException("Accessor mixinMethodNode NULL!");
        }

        mixinMethodNode.access &= ~ACC_NATIVE;
        mixinMethodNode.instructions.clear();

        InsnList insnList = new InsnList();



        insnList.add(new LdcInsnNode(key));
        if(!isStatic)
            insnList.add(new VarInsnNode(ALOAD, 0));
        else insnList.add(new InsnNode(ACONST_NULL));

        Class<?> returnType = object.getReturnType();
        int valueSlot = isStatic ? 0 : 1;


        if(isMethod) {
            insnList.add(new MethodInsnNode(
                    INVOKESTATIC,
                    Type.getInternalName(VarHandleCache.class),
                    "getField",
                    "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;",
                    false
            ));

        } else {

        }

        if(returnType == void.class) {
            //setter
            Class<?> paramType = object.getParameterTypes()[1];
//            System.out.println("1111111111111111111 " + paramType.getName());
            if (paramType == boolean.class || paramType == int.class
                    || paramType == byte.class || paramType == short.class
                    || paramType == char.class) {
                insnList.add(new VarInsnNode(ILOAD, valueSlot));
                if (paramType == boolean.class) {
                    insnList.add(new MethodInsnNode(INVOKESTATIC,
                            "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
                } else {
                    insnList.add(new MethodInsnNode(INVOKESTATIC,
                            "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
                }
            } else if (paramType == long.class) {
                insnList.add(new VarInsnNode(LLOAD, valueSlot));
                insnList.add(new MethodInsnNode(INVOKESTATIC,
                        "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false));
            } else if (paramType == float.class) {
                insnList.add(new VarInsnNode(FLOAD, valueSlot));
                insnList.add(new MethodInsnNode(INVOKESTATIC,
                        "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false));
            } else if (paramType == double.class) {
                insnList.add(new VarInsnNode(DLOAD, valueSlot));
                insnList.add(new MethodInsnNode(INVOKESTATIC,
                        "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false));
            } else {
                insnList.add(new VarInsnNode(ALOAD, valueSlot));
            }
            insnList.add(new MethodInsnNode(
                    INVOKESTATIC,
                    Type.getInternalName(VarHandleCache.class),
                    "setField",
                    "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
                    false
            ));
            insnList.add(new InsnNode(RETURN));
        }
        else {
            insnList.add(new MethodInsnNode(
                    INVOKESTATIC,
                    Type.getInternalName(VarHandleCache.class),
                    "getField",
                    "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;",
                    false
            ));

            if (returnType == boolean.class) {
                insnList.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL,
                        "java/lang/Boolean", "booleanValue", "()Z", false));
                insnList.add(new InsnNode(IRETURN));
            } else if (returnType == int.class) {
                insnList.add(new TypeInsnNode(CHECKCAST, "java/lang/Integer"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL,
                        "java/lang/Integer", "intValue", "()I", false));
                insnList.add(new InsnNode(IRETURN));
            } else if (returnType == long.class) {
                insnList.add(new TypeInsnNode(CHECKCAST, "java/lang/Long"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL,
                        "java/lang/Long", "longValue", "()J", false));
                insnList.add(new InsnNode(LRETURN));
            } else if (returnType == float.class) {
                insnList.add(new TypeInsnNode(CHECKCAST, "java/lang/Float"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL,
                        "java/lang/Float", "floatValue", "()F", false));
                insnList.add(new InsnNode(FRETURN));
            } else if (returnType == double.class) {
                insnList.add(new TypeInsnNode(CHECKCAST, "java/lang/Double"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL,
                        "java/lang/Double", "doubleValue", "()D", false));
                insnList.add(new InsnNode(DRETURN));
            } else {
                insnList.add(new TypeInsnNode(CHECKCAST,
                        Type.getInternalName(returnType)));
                insnList.add(new InsnNode(ARETURN));
            }
        }

        mixinMethodNode.instructions.add(insnList);
    }
    private void processMethod(String methodName, String desc) {

    }
    private void processField() {

    }
    @Override
    public boolean transformMixinClass() {
        return true;
    }
}
