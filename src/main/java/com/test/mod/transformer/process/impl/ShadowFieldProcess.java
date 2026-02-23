package com.test.mod.transformer.process.impl;

import com.test.mod.asm.Opcodes;
import com.test.mod.asm.Type;
import com.test.mod.asm.tree.*;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.TransformerException;
import com.test.mod.transformer.annotation.Shadow;
import com.test.mod.transformer.mapping.Mapping;
import com.test.mod.transformer.process.TransformerProcess;
import com.test.mod.transformer.utils.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ShadowFieldProcess extends TransformerProcess<Shadow, Field> {
    public ShadowFieldProcess() {
        super(Shadow.class, Field.class);
    }

    @Override
    public void process(ClassNode targetClassNode, ClassNode mixinClassNode,Class<?> targetClass, ITransformer iTransformer, Field field, Shadow annotation) {
        String fieldName = field.getName();
        String fieldDesc = Type.getDescriptor(field.getType());

        FieldNode targetField = null;
        for (String s : annotation.value()) {
            if(annotation.remap())
                s = Mapping.get(targetClass, s, null);
            targetField = Tools.getField(targetClassNode, fieldDesc, s);
            if (targetField != null) break;
        }
        if (targetField == null)
            throw new TransformerException("Shadow field not found in target: " + fieldName);

        boolean shadowStatic = Modifier.isStatic(field.getModifiers());
        boolean targetStatic = (targetField.access & Opcodes.ACC_STATIC) != 0;
        if (shadowStatic != targetStatic)
            throw new TransformerException("Shadow static mismatch: " + fieldName);


        rewriteOwner(targetClass, targetClassNode, mixinClassNode, field, annotation);
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
