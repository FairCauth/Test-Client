package com.test.mod.transformer.process.impl;

import com.fair.preload.Preloader;
import com.test.mod.Main;
import com.test.mod.asm.Opcodes;
import com.test.mod.asm.Type;
import com.test.mod.asm.tree.*;
import com.test.mod.transformer.TransformerException;
import com.test.mod.transformer.annotation.Shadow;
import com.test.mod.transformer.mapping.forge.Mapping;
import com.test.mod.transformer.process.ProcessInfo;
import com.test.mod.transformer.process.TransformerProcess;
import com.test.mod.transformer.utils.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ShadowFieldProcess extends TransformerProcess<Shadow, Field> {
    public ShadowFieldProcess() {
        super(Shadow.class, Field.class);
    }

    @Override
    public void process(ProcessInfo processInfo, Field field, Shadow annotation) {
        String fieldName = field.getName();
        String fieldDesc = Type.getDescriptor(field.getType());

        FieldNode targetField = null;
        for (String s : annotation.value()) {
            if(annotation.remap())
            {
                if(Main.mcEnvironment == Main.McEnvironment.FORGE_OBF) {
                    s = Mapping.get(processInfo.targetClass(), s, null);
                } else if (Main.mcEnvironment == Main.McEnvironment.VANILLA_OBF) {
                    String owner = processInfo.originalClassName().replace(".", "/");
                    s = Main.mapping.mapFieldName(owner, s, fieldDesc);
                    fieldDesc = Main.mapping.mapDesc(fieldDesc);
                } else if (Main.mcEnvironment == Main.McEnvironment.FABRIC_OBF) {
                    String owner = processInfo.originalClassName().replace(".", "/");
                    s = Main.fabric_mapping.mapFieldName(owner, s, fieldDesc);
                    fieldDesc = Main.fabric_mapping.mapDesc(fieldDesc);
                }
//                s = Mapping.get(targetClass, s, null);
            }
            targetField = Tools.getField(processInfo.targetClassNode(), fieldDesc, s);
            if (targetField != null) break;
        }
        if (targetField == null)
            throw new TransformerException("Shadow field not found in target: " + fieldName);

        boolean shadowStatic = Modifier.isStatic(field.getModifiers());
        boolean targetStatic = (targetField.access & Opcodes.ACC_STATIC) != 0;
        if (shadowStatic != targetStatic)
            throw new TransformerException("Shadow static mismatch: " + fieldName);


        rewriteOwner(processInfo.targetClass(), processInfo.targetClassNode(), processInfo.mixinClassNode(), field, annotation);
    }
    private void rewriteOwner(
            Class<?> targetClass,
            ClassNode target,
            ClassNode mixin,
            Field shadowField,
            Shadow annotation
    ) {
        String shadowName = shadowField.getName();
        String shadowDesc = Type.getDescriptor(shadowField.getType());
        boolean founded = false;
        for (String s : annotation.value()) {

            for (MethodNode method : mixin.methods) {
                for (AbstractInsnNode insn : method.instructions.toArray()) {
                    if (insn instanceof FieldInsnNode f) {
                        if (f.owner.equals(mixin.name) && f.name.equals(shadowName) && f.desc.equals(shadowDesc)) {
                            founded = true;
                            Preloader.send("Rewrite Shadow field: " + f.owner + " -> " + target.name);
                            System.out.println("Rewrite Shadow field: " + f.owner + " -> " + target.name);
                            if (annotation.remap())
                                s = Mapping.get(targetClass, s, null);
                            f.owner = target.name;
                            f.name = s;

                        }
                    }
                }
            }
            if(founded) break;
        }

    }

}
